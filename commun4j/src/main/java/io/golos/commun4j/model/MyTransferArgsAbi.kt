package io.golos.commun4j.model

import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.AssetCompress
import io.golos.commun4j.abi.writer.NameCompress
import io.golos.commun4j.abi.writer.StringCompress

@Abi
internal data class MyTransferArgsAbi(
        val from: String,
        val to: String,
        val quantity: String,
        val memo: String
) {

    val getFrom: String
        @NameCompress get() = from

    val getTo: String
        @NameCompress get() = to

    val getQuantity: String
        @AssetCompress get() = quantity

    val getMemo: String
        @StringCompress get() = memo
}