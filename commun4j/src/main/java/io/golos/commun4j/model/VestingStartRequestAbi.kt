package io.golos.commun4j.model

import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ByteCompress
import io.golos.commun4j.abi.writer.BytesCompress
import io.golos.commun4j.abi.writer.NameCompress
import io.golos.commun4j.sharedmodel.CyberName

@Abi
internal class VestingStartRequestAbi(private val owner: CyberName,
                                      private val ramPayer: CyberName,
                                      private val precision: Byte) {

    val getOwner: String
        @NameCompress get() = owner.name

    val getPrecision: Byte
        @ByteCompress get() = precision

    val decsBytes: ByteArray
        @BytesCompress
        get() = "GOLOS".toByteArray().plus(0).plus(0)


    val getRamPayer: String
        @NameCompress
        get() = ramPayer.name


}