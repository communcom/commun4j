/**
 * Copyright 2013-present memtrip LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.golos.commun4j.chain.actions.transaction.account

import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.AbiBinaryGenTransactionWriter
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.chain.actions.transaction.account.actions.buyram.BuyRamBytesArgs
import io.golos.commun4j.chain.actions.transaction.account.actions.buyram.BuyRamBytesBody
import io.golos.commun4j.chain.actions.transaction.account.actions.delegatebw.DelegateBandwidthArgs
import io.golos.commun4j.chain.actions.transaction.account.actions.delegatebw.DelegateBandwidthBody
import io.golos.commun4j.chain.actions.transaction.account.actions.newaccount.AccountKeyAbi
import io.golos.commun4j.chain.actions.transaction.account.actions.newaccount.AccountRequiredAuthAbi
import io.golos.commun4j.chain.actions.transaction.account.actions.newaccount.NewAccountArgs
import io.golos.commun4j.chain.actions.transaction.account.actions.newaccount.NewAccountBody
import io.golos.commun4j.core.crypto.EosPublicKey
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.reactivex.Single
import java.util.Arrays.asList

class CreateAccountChain(chainApi: io.golos.commun4j.http.rpc.ChainApi) : io.golos.commun4j.chain.actions.transaction.ChainTransaction(chainApi) {

    data class Args(
            val newAccountName: String,
            val quantity: Quantity,
            val ownerPublicKey: EosPublicKey,
            val activePublicKey: EosPublicKey,
            val transfer: Boolean
    ) {
        data class Quantity(
                val ram: Long,
                val net: String,
                val cpu: String
        )
    }

    fun createAccount(
            args: Args,
            transactionContext: io.golos.commun4j.chain.actions.transaction.TransactionContext,
            extraActionAbi: List<ActionAbi> = emptyList()
    ): Single<io.golos.commun4j.chain.actions.ChainResponse<TransactionCommitted<Any>>> {

        return push(
                transactionContext.expirationDate,
                with(ArrayList<ActionAbi>(asList(
                        ActionAbi(
                                "eosio",
                                "newaccount",
                                asList(TransactionAuthorizationAbi(
                                        transactionContext.authorizingAccountName,
                                        "active")
                                ),
                                newAccountAbi(args, transactionContext)
                        ),
                        ActionAbi(
                                "eosio",
                                "buyrambytes",
                                asList(TransactionAuthorizationAbi(
                                        transactionContext.authorizingAccountName,
                                        "active")
                                ),
                                buyRamBytesAbi(args, transactionContext)
                        ),
                        ActionAbi(
                                "eosio",
                                "delegatebw",
                                asList(TransactionAuthorizationAbi(
                                        transactionContext.authorizingAccountName,
                                        "active")
                                ),
                                delegateBandwidthAbi(args, transactionContext)
                        )
                ))) {
                    addAll(extraActionAbi)
                    this
                },
                transactionContext.authorizingPrivateKey
        )
    }

    private fun newAccountAbi(args: Args, transactionContext: io.golos.commun4j.chain.actions.transaction.TransactionContext): String {
        return AbiBinaryGenTransactionWriter(CompressionType.NONE).squishNewAccountBody(
                NewAccountBody(
                        NewAccountArgs(
                                transactionContext.authorizingAccountName,
                                args.newAccountName,
                                AccountRequiredAuthAbi(
                                        1,
                                        asList(
                                                AccountKeyAbi(
                                                        args.ownerPublicKey.toString(),
                                                        1)
                                        ),
                                        emptyList(),
                                        emptyList()
                                ),
                                AccountRequiredAuthAbi(
                                        1,
                                        asList(
                                                AccountKeyAbi(
                                                        args.activePublicKey.toString(),
                                                        1)
                                        ),
                                        emptyList(),
                                        emptyList()
                                )
                        )
                )
        ).toHex()
    }

    private fun buyRamBytesAbi(args: Args, transactionContext: io.golos.commun4j.chain.actions.transaction.TransactionContext): String {
        return AbiBinaryGenTransactionWriter(CompressionType.NONE).squishBuyRamBytesBody(
                BuyRamBytesBody(
                        BuyRamBytesArgs(
                                transactionContext.authorizingAccountName,
                                args.newAccountName,
                                args.quantity.ram)
                )
        ).toHex()
    }

    private fun delegateBandwidthAbi(args: Args, transactionContext: io.golos.commun4j.chain.actions.transaction.TransactionContext): String {
        return AbiBinaryGenTransactionWriter(CompressionType.NONE).squishDelegateBandwidthBody(
                DelegateBandwidthBody(
                        DelegateBandwidthArgs(
                                transactionContext.authorizingAccountName,
                                args.newAccountName,
                                args.quantity.net,
                                args.quantity.cpu,
                                if (args.transfer) 1 else 0
                        )
                )
        ).toHex()
    }
}