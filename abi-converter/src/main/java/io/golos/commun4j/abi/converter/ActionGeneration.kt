package io.golos.commun4j.abi.converter

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.AbiBinaryGenTransactionWriter
import io.golos.commun4j.chain.actions.transaction.TransactionPusher
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.core.crypto.EosPrivateKey
import io.golos.commun4j.sharedmodel.Commun4jConfig
import io.golos.commun4j.sharedmodel.EosAbi

const val contractNameProperty = "contractName"

const val actionNameProperty = "actionName"

fun generateActions(eosAbi: EosAbi,
                    packageName: String): List<FileSpec> {
    return eosAbi.abi.actions.map { abiAction ->
        val actionName = eosAbi.toActionName(abiAction.name)

        val abiType = ClassName(packageName,
                eosAbi.toStructName(abiAction.type)
        )
        val structParamName = "struct"

        FileSpec
                .builder(packageName, actionName)
                .addComment("class is generated, and would be overridden on compile")
                .addImport(ActionAbi::class.asTypeName(), "")
                .addImport(TransactionPusher::class.asTypeName(), "")
                .addImport(CompressionType::class.asTypeName(), "")
                .addImport(AbiBinaryGenTransactionWriter::class, "")
                .addImport("io.golos.commun4j.abi.implementation", "createBandwidthActionAbi")

                .addType(TypeSpec.classBuilder(actionName)
                        .primaryConstructor(FunSpec.constructorBuilder()
                                .addParameter(structParamName, abiType)
                                .build())


                        .addType(TypeSpec.companionObjectBuilder()
                                .addProperty(PropertySpec.builder(actionNameProperty, String::class)
                                        .addAnnotation(JvmStatic::class)
                                        .initializer("\"${abiAction.name}\"")
                                        .build())

                                .addProperty(PropertySpec.builder(contractNameProperty, String::class)
                                        .addAnnotation(JvmStatic::class)
                                        .initializer("\"${eosAbi.account_name.name}\"")
                                        .build())
                                .build())
                        .addProperty(PropertySpec.builder(structParamName, abiType)
                                .initializer(structParamName)
                                .build())
                        .addFunction(FunSpec
                                .builder("toActionAbi")
                                .addAnnotation(JvmOverloads::class.java)
                                .addParameter("transactionAuth", List::class.asClassName().parameterizedBy(TransactionAuthorizationAbi::class.asClassName()))

                                .addParameter(
                                        ParameterSpec.builder("contractName", String::class)
                                                .defaultValue("Companion.$contractNameProperty")
                                                .build())
                                .addParameter(
                                        ParameterSpec.builder("actionName", String::class)
                                                .defaultValue("Companion.$actionNameProperty")
                                                .build()
                                )

                                .addCode("""return  ActionAbi(contractName, actionName,
            transactionAuth, struct.toHex())""".trimMargin())
                                .build())

                        .addFunction(
                                FunSpec.builder("createSignedTransactionForProvideBw")
                                        .addAnnotation(JvmOverloads::class.java)
                                        .addParameter("transactionAuth",
                                                List::class.asClassName().parameterizedBy(TransactionAuthorizationAbi::class.asClassName()))
                                        .addParameter("key", EosPrivateKey::class)
                                        .addParameter(
                                                ParameterSpec.builder("withConfig", Commun4jConfig::class)
                                                        .build())
                                        .addParameter(
                                                ParameterSpec.builder("provideBandwidth",
                                                        ClassName("io.golos.commun4j.abi.implementation", "BandWidthProvideOption"))
                                                        .build())
                                        .addParameter(
                                                ParameterSpec.builder("contractName", String::class)
                                                        .defaultValue("Companion.$contractNameProperty")
                                                        .build())
                                        .addParameter(
                                                ParameterSpec.builder("actionName", String::class)
                                                        .defaultValue("Companion.$actionNameProperty")
                                                        .build())
                                        .addCode("""return TransactionPusher.createSignedTransaction(
            listOf(toActionAbi(transactionAuth, contractName, actionName),
                    createBandwidthActionAbi(transactionAuth[0].actor, provideBandwidth.provider)),
            listOf(key),
            withConfig.blockChainHttpApiUrl,
            withConfig.logLevel,
            withConfig.httpLogger)""")
                                        .build()
                        )
                        .addFunction(FunSpec.builder("push")
                                .addAnnotation(JvmOverloads::class.java)
                                .addParameter("transactionAuth", List::class.asClassName().parameterizedBy(TransactionAuthorizationAbi::class.asClassName()))
                                .addParameter("key", EosPrivateKey::class)
                                .addParameter(
                                        ParameterSpec.builder("withConfig", Commun4jConfig::class)
                                                .build())
                                .addParameter(
                                        ParameterSpec.builder("provideBandwidth",
                                                ClassName("io.golos.commun4j.abi.implementation", "BandWidthProvideOption")
                                                        .copy(true))
                                                .defaultValue("null")
                                                .build())
                                .addParameter(
                                        ParameterSpec.builder("bandwidthProviderKey", EosPrivateKey::class.asTypeName().copy(true))
                                                .defaultValue("null")
                                                .build()
                                )
                                .addParameter(
                                        ParameterSpec.builder("contractName", String::class)
                                                .defaultValue("Companion.$contractNameProperty")
                                                .build())
                                .addParameter(
                                        ParameterSpec.builder("actionName", String::class)
                                                .defaultValue("Companion.$actionNameProperty")
                                                .build())

                                .addCode("""return TransactionPusher.pushTransaction(arrayListOf(toActionAbi(transactionAuth,
            contractName, actionName)).apply { if (provideBandwidth != null) this.add(createBandwidthActionAbi(transactionAuth[0].actor, provideBandwidth.provider)) },
            key, struct::class.java,
            withConfig.blockChainHttpApiUrl, provideBandwidth != null, bandwidthProviderKey,
            withConfig.logLevel,
            withConfig.httpLogger)""".trimIndent())

                                .build())
                        .build())
                .build()
    }
}