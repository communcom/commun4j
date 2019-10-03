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
package io.golos.commun4j.chain.actions.transaction.abi

import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ChainIdCompress
import io.golos.commun4j.abi.writer.ChildCompress
import io.golos.commun4j.abi.writer.HexCollectionCompress

@Abi
data class SignedTransactionAbi(
        val chainId: String,
        val transaction: TransactionAbi,
        val context_free_data: List<String>
) {
    val getChainId: String
        @ChainIdCompress get() = chainId

    val getTransaction: TransactionAbi
        @ChildCompress get() = transaction

    val getContextFreeData: List<String>
        @HexCollectionCompress get() = context_free_data
}