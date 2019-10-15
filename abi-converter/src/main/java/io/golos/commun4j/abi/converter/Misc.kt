package io.golos.commun4j.abi.converter

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import io.golos.commun4j.abi.writer.*
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberNameAdapter
import io.golos.commun4j.sharedmodel.EosAbi
import io.golos.commun4j.sharedmodel.ISquishable
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.math.BigInteger
import java.util.*


class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val srcDir = args[0]

            val buildDir = File(args[1])

            val contracts = args.toList().subList(2, args.size)

            println("src dir = $srcDir, buildDir = $buildDir, contracts = $contracts")

            val abis = contracts.map { getAbi(CyberName(it), buildDir) }

            val destPackage = "io.golos.commun4j.abi.implementation"

            val generatedFileSpec = abis
                    .map { abi: EosAbi ->
                        val abiPackage = "$destPackage.${abi.toContractAccount().name!!}.${abi.toContractName().name!!}"

                        (generateClasses(
                                abi,
                                abiPackage,
                                destPackage,
                                "Cyber")
                                + generateActions(abi, abiPackage)
                                )
                    }.flatten()
            generatedFileSpec.forEach {
                it.writeTo(File(srcDir))
            }
        }
    }
}


private val moshi = Moshi.Builder().add(CyberName::class.java, CyberNameAdapter()).build()!!

fun getAbi(contractName: CyberName, buildDir: File): EosAbi {
    val cashedFile = File(buildDir, "${contractName.name}.json")

    if (cashedFile.exists()) {
        val content = cashedFile.readText()
        return moshi.fromJson<EosAbi>(content)!!
    }
    val okHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()


    val resp = okHttpClient.newCall(Request.Builder()
            .post(RequestBody.create(MediaType.get("application/json"),
                    moshi.toJson(mapOf("account_name" to contractName.name))))
            .url("http://116.202.4.46:8888/v1/chain/get_abi")
            .build())
            .execute()

    val respBody = resp.body()!!.string()

    if (!resp.isSuccessful)
        throw java.lang.IllegalStateException("blockchain said that $contractName does not exists")

    val out = moshi.fromJson<EosAbi>(respBody)!!
    cashedFile.writeText(respBody)

    return out
}

val squishableInterfaceName: ClassName = ISquishable::class.asClassName()

val squishMethodName = "squish"

val structNamePropertyName = "structName"

val getStructIndexFuncName = "getStructIndexForCollectionSquish"

fun generateClasses(eosAbi: EosAbi,
                    packageName: String,
                    abiBinaryPackage: String,
                    writerInterfaceName: String): List<FileSpec> {

    val stringToTypesMap = HashMap(builtInTypes)

    val variantsMap = eosAbi // map of associations of implementation of variant
            // i.e interface -> [struct1:interface, struct2:interface]
            .abi
            .variants.map { abiVarian -> //interface

        val interfaceName = eosAbi.toInterfaceName(abiVarian.name)

        stringToTypesMap.putAll(ClassName(packageName, interfaceName).createVariations(abiVarian.name))

        interfaceName to abiVarian.types.map { variant ->
            eosAbi.toStructName(variant)
        }
    }.toMap()

    val out = variantsMap.keys.map {
        FileSpec.builder(packageName, it)
                .addType(TypeSpec
                        .interfaceBuilder(it)
                        .addSuperinterface(squishableInterfaceName)
                        .addAnnotation(Abi::class.asTypeName())
                        .build())
                .build()
    }

    eosAbi.abi.types.forEach {
        val type = it.type
        val resolvedClassName =
                if (!stringToTypesMap.containsKey(it.type) && type.matches(integerRegex)) {
                    intStringToClassName(it.type)
                } else stringToTypesMap[it.type]
                        ?: throw IllegalStateException("type ${it.type} not found")
        stringToTypesMap.putAll(resolvedClassName.createVariations(it.new_type_name))
    }


    eosAbi.abi.structs.forEach { abiStruct ->
        stringToTypesMap.putAll(ClassName(packageName,
                eosAbi.toStructName(abiStruct.name)).createVariations(abiStruct.name))
    }

    return out + eosAbi.abi.structs.map { abiStruct ->
        var isInterfaceImpl = false

        val className = eosAbi.toStructName(abiStruct.name)

        val classFile = FileSpec.builder(packageName, className)
                .addComment("Class is generated, changes would be overridden on compile")
                .addImport(CompressionType::class, "")
                .addImport(ActionAbi::class, "")
                .addImport(abiBinaryPackage, "AbiBinaryGen$writerInterfaceName")
                .addType(TypeSpec.classBuilder(className)
                        .also { typeBuilder ->
                            if (variantsMap.values.flatten().contains(className)) {
                                typeBuilder.addSuperinterfaces(variantsMap.filter { it.value.contains(className) }.map {
                                    ClassName(packageName, it.key)
                                })
                                isInterfaceImpl = true
                            }
                        }
                        .addModifiers(KModifier.DATA)
                        .addAnnotation(Abi::class.asTypeName())
                        .addAnnotation(AnnotationSpec.builder(JsonClass::class).addMember("generateAdapter = true").build())
                        .primaryConstructor(FunSpec.constructorBuilder()
                                .also { builder: FunSpec.Builder ->
                                    val fields = resolveStrucFilelds(abiStruct, eosAbi)

                                    fields.forEach { stuctField ->
                                        builder.addParameter(stuctField.name,
                                                stringToTypesMap[stuctField.type]
                                                        ?: throw java.lang.IllegalStateException("cannot find ClassName for type ${stuctField.type}"))
                                    }
                                    if (fields.isEmpty())
                                        builder.addParameter(ParameterSpec.builder("stub",
                                                String::class.asTypeName())
                                                .defaultValue("\"stub\"")
                                                .build())
                                }
                                .build())
                        .also { builder: TypeSpec.Builder ->
                            val fields = resolveStrucFilelds(abiStruct, eosAbi)

                            fields.forEach { structField ->
                                builder.addProperty(
                                        PropertySpec
                                                .builder(structField.name,
                                                        stringToTypesMap[structField.type]!!)
                                                .initializer(structField.name)
                                                .build())
                            }

                            builder.addProperty(PropertySpec.builder(structNamePropertyName, String::class)
                                    .initializer("\"${abiStruct.name}\"")
                                    .build())

                            if (isInterfaceImpl) {
                                val variants = eosAbi.abi.variants
                                val index = variants.find { it.types.contains(abiStruct.name) }!!
                                        .types.indexOf(abiStruct.name)
                                if (index < 0) throw IllegalStateException("index cannot be -1")

                                builder.addFunction(FunSpec.builder(getStructIndexFuncName)
                                        .returns(Int::class)
                                        .addModifiers(KModifier.OVERRIDE)
                                        .addCode("return $index")
                                        .build())
                            }

                            if (fields.isEmpty())
                                builder.addProperty(PropertySpec.builder("stub",
                                        String::class.asTypeName())
                                        .initializer("stub")
                                        .build())
                        }
                        .also { builder: TypeSpec.Builder ->
                            val fields = resolveStrucFilelds(abiStruct, eosAbi)

                            fields.forEach { structField ->

                                val typeName = stringToTypesMap[structField.type]!!
                                builder.addProperty(
                                        PropertySpec.builder("get${structField.name.fromSnakeCase().capitalize()}",
                                                when (typeName) {
                                                    BigInteger::class.asTypeName() -> ByteArray::class.asTypeName()
                                                    else -> typeName
                                                })
                                                .getter(FunSpec.getterBuilder()
                                                        .addStatement(
                                                                when (typeName) {
                                                                    BigInteger::class.asTypeName() ->
                                                                        "return ByteArray(16) { 0 }.also { System.arraycopy(${structField.name}.toByteArray(), 0, it, 0, ${structField.name}.toByteArray().size) }.reversedArray()"
                                                                    else -> "return ${structField.name}"
                                                                })
                                                        .addAnnotation(
                                                                when {
                                                                    simpleTypeToAnnotationsMap.containsKey(typeName) -> simpleTypeToAnnotationsMap.getValue(typeName)
                                                                    else -> {
                                                                        when (typeName) {
                                                                            is ClassName -> ChildCompress::class
                                                                            is ParameterizedTypeName -> {
                                                                                if (typeName.typeArguments.size != 1) throw IllegalArgumentException("wrong type arguments size, " +
                                                                                        "now ${typeName.typeArguments}")

                                                                                val collectionType = typeName.typeArguments.first()

                                                                                val simpleCollectionType = collectionType.toString().takeLastWhile { it != '.' }
                                                                                if (variantsMap.keys.contains(simpleCollectionType)) InterfaceCollectionCompress::class
                                                                                else when (collectionType) {
                                                                                    String::class.asTypeName() -> StringCollectionCompress::class
                                                                                    Long::class.asTypeName() -> LongCollectionCompress::class
                                                                                    CyberName::class.asTypeName() -> CyberNameCollectionCompress::class
                                                                                    Byte::class.asTypeName() -> ByteCompress::class
                                                                                    Int::class.asTypeName() -> IntCollectionCompress::class
                                                                                    Short::class.asTypeName() -> ShortCollectionCompress::class
                                                                                    else -> CollectionCompress::class
                                                                                }
                                                                            }
                                                                            else -> throw java.lang.IllegalStateException("cannot find right annotation for " +
                                                                                    "type $typeName")
                                                                        }
                                                                    }
                                                                }
                                                        )
                                                        .also {
                                                            if (typeName.isNullable)it.addAnnotation(NullableCompress::class)
                                                        }
                                                        .build())
                                                .build())
                            }
                        }
                        .also { builder ->
                            builder.addFunction(FunSpec
                                    .builder("toHex")
                                    .addCode("""return AbiBinaryGen$writerInterfaceName(CompressionType.NONE)
                                        |               .squish$className(this, false)
                                        |               .toHex()
                                    """.trimMargin())
                                    .build())
                            if (isInterfaceImpl) {
                                builder.addFunction(FunSpec
                                        .builder(squishMethodName)
                                        .addModifiers(KModifier.OVERRIDE)
                                        .addCode("""return AbiBinaryGen$writerInterfaceName(CompressionType.NONE)
                                        |               .squish$className(this, false)
                                        |               .toBytes()
                                    """.trimMargin())
                                        .build())
                            }

                            builder.addFunction(FunSpec
                                    .builder("toActionAbi")
                                    .addParameter("contractName", String::class)
                                    .addParameter("actionName", String::class)
                                    .addParameter("transactionAuth", List::class.asClassName().parameterizedBy(TransactionAuthorizationAbi::class.asClassName()))
                                    .addCode("""
                                        |return ActionAbi(contractName, actionName,
                                        |       transactionAuth, toHex())""".trimMargin())
                                    .build())
                        }
                        .build())
                .build()
        classFile
    }
}


