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
package io.golos.commun4j.chain.actions.transaction

import io.golos.commun4j.abi.writer.bytewriter.DefaultByteWriter
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.SignedTransactionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAbi
import io.golos.commun4j.core.block.BlockIdDetails
import io.golos.commun4j.core.crypto.EosPrivateKey
import io.golos.commun4j.core.crypto.signature.PrivateKeySigning
import io.golos.commun4j.core.hex.DefaultHexWriter
import io.golos.commun4j.http.rpc.model.signing.PushTransaction
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import com.squareup.moshi.Moshi
import io.reactivex.Single
import retrofit2.Response
import java.util.*
import java.util.Arrays.asList

abstract class ChainTransaction(
        private val chainApi: io.golos.commun4j.http.rpc.ChainApi
) {

    private val moshi: Moshi = Moshi.Builder().build()

    fun push(
            expirationDate: Date,
            actions: List<ActionAbi>,
            authorizingPrivateKey: EosPrivateKey
    ): Single<io.golos.commun4j.chain.actions.ChainResponse<TransactionCommitted<Any>>> {
        return chainApi.getInfo().flatMap { info ->
            if (info.isSuccessful) {
                val transaction = transaction(
                        expirationDate,
                        BlockIdDetails(info.body()!!.head_block_id),
                        actions)

                val signature = PrivateKeySigning().sign(
                        AbiBinaryGenTransactionWriter(DefaultByteWriter(1048), DefaultHexWriter(), CompressionType.NONE).squishSignedTransactionAbi(
                                SignedTransactionAbi(
                                        info.body()!!.chain_id,
                                        transaction,
                                        emptyList()
                                )
                        ).toBytes(), authorizingPrivateKey)

                chainApi.pushTransaction(
                        PushTransaction(
                                asList(signature),
                                "none",
                                "",
                                AbiBinaryGenTransactionWriter(CompressionType.NONE).squishTransactionAbi(transaction).toHex()))
            } else {
                Single.just(Response.error(info.code(), info.errorBody()!!))
            }
        }.map {

            var error: io.golos.commun4j.chain.actions.ChainError? = null
            if (it.errorBody() != null) {
                error = moshi.adapter(io.golos.commun4j.chain.actions.ChainError::class.java).fromJson(it.errorBody()!!.string())
            }

            io.golos.commun4j.chain.actions.ChainResponse(
                    it.isSuccessful,
                    it.code(),
                    it.body(),
                    error)
        }
    }

    private fun transaction(
            expirationDate: Date,
            blockIdDetails: BlockIdDetails,
            actions: List<ActionAbi>
    ): TransactionAbi {

        return TransactionAbi(
                expirationDate,
                blockIdDetails.blockNum,
                blockIdDetails.blockPrefix,
                0,
                0,
                0,
                0,
                0,
                emptyList(),
                actions,
                emptyList(),
                emptyList(),
                emptyList())
    }
}