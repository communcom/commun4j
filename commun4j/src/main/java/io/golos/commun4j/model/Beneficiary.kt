package io.golos.commun4j.model

import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.NameCompress
import io.golos.commun4j.abi.writer.ShortCompress
import io.golos.commun4j.sharedmodel.CyberName

@Abi
class Beneficiary(private val account: CyberName,
                  private val deductprcnt: Short) {


    val getAccount: String
        @NameCompress get() = account.name

    val getDeduct: Short
        @ShortCompress get() = deductprcnt

}
