package io.golos.commun4j.sharedmodel

data class EosAbi(val account_name: CyberName,
                  val abi: AbiContent)

data class AbiContent(val version: String,
                      val types: List<AbiType>,
                      val structs: List<AbiStruct>,
                      val actions: List<AbiAction>,
                      val variants: List<AbiVariant>)

data class AbiVariant(val name: String, val types: List<String>)

data class AbiType(val new_type_name: String,
                   val type: String)

data class AbiStruct(val name: String,
                     val base: String,
                     val fields: List<StructField>)

data class StructField(val name: String,
                       val type: String)

data class AbiAction(val name: String,
                     val type: String)