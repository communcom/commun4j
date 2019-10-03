package io.golos.commun4j.http.rpc.utils.testabi

import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ChildCompress

@Abi
data class TransferBody(
    val args: TransferArgs
) {

    val getArgs: TransferArgs
        @ChildCompress get() = args
}