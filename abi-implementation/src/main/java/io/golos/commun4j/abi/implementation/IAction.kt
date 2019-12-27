package io.golos.commun4j.abi.implementation

import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi

interface IAction {

    fun asActionAbi(
            transactionAuth: List<TransactionAuthorizationAbi>
    ): ActionAbi
}