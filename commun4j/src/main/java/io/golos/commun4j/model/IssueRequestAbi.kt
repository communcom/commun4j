package io.golos.commun4j.model

import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.AssetCompress
import io.golos.commun4j.abi.writer.NameCompress
import io.golos.commun4j.abi.writer.StringCompress
import io.golos.commun4j.sharedmodel.CyberName

@Abi
class IssueRequestAbi(private val to: CyberName,
                      private val quantity: String,
                      private val memo: String) {

    val getForUser: String
        @NameCompress get() = to.name

    val getAmount: String
        @AssetCompress get() = quantity
    val getMemo: String
        @StringCompress get() = memo
}