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
package io.golos.commun4j.chain.actions.transaction.vote

import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.AbiBinaryGenTransactionWriter
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.chain.actions.transaction.vote.actions.VoteArgs
import io.golos.commun4j.chain.actions.transaction.vote.actions.VoteBody
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted

import io.reactivex.Single
import java.util.Arrays.asList

class VoteChain(chainApi: io.golos.commun4j.http.rpc.ChainApi) : io.golos.commun4j.chain.actions.transaction.ChainTransaction(chainApi) {

    data class Args(
        val voter: String,
        val proxy: String,
        val producers: List<String>
    )

    fun vote(
        args: Args,
        transactionContext: io.golos.commun4j.chain.actions.transaction.TransactionContext
    ): Single<io.golos.commun4j.chain.actions.ChainResponse<TransactionCommitted<Any>>> {

        return push(
            transactionContext.expirationDate,
            asList(ActionAbi(
                "eosio",
                "voteproducer",
                asList(TransactionAuthorizationAbi(
                    transactionContext.authorizingAccountName,
                    "active")),
                voteBin(args)
            )),
            transactionContext.authorizingPrivateKey
        )
    }

    private fun voteBin(args: Args): String {
        return AbiBinaryGenTransactionWriter(CompressionType.NONE).squishVoteBody(
            VoteBody(
                VoteArgs(
                    args.voter,
                    args.proxy,
                    args.producers)
            )
        , false).toHex()
    }
}