package io.golos.commun4j.chain.actions.transaction.account

import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.AbiBinaryGenTransactionWriter
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.chain.actions.transaction.account.actions.refund.RefundArgs
import io.golos.commun4j.chain.actions.transaction.account.actions.refund.RefundBody
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.reactivex.Single
import java.util.Arrays.asList

class RefundChain(chainApi: io.golos.commun4j.http.rpc.ChainApi) : io.golos.commun4j.chain.actions.transaction.ChainTransaction(chainApi) {

    fun refund(
            transactionContext: io.golos.commun4j.chain.actions.transaction.TransactionContext
    ): Single<io.golos.commun4j.chain.actions.ChainResponse<TransactionCommitted<Any>>> {

        return push(
                transactionContext.expirationDate,
                asList(ActionAbi(
                        "eosio",
                        "refund",
                        asList(TransactionAuthorizationAbi(
                                transactionContext.authorizingAccountName,
                                "active")),
                        refundAbi(transactionContext)
                )),
                transactionContext.authorizingPrivateKey
        )
    }

    private fun refundAbi(transactionContext: io.golos.commun4j.chain.actions.transaction.TransactionContext): String {
        return AbiBinaryGenTransactionWriter(CompressionType.NONE).squishRefundBody(
                RefundBody(RefundArgs(transactionContext.authorizingAccountName))
        ).toHex()
    }
}