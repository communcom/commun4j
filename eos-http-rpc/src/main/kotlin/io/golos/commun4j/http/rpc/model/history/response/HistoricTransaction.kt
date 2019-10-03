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
package io.golos.commun4j.http.rpc.model.history.response

import io.golos.commun4j.http.rpc.model.transaction.response.TransactionActionTrace
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class HistoricTransaction(
    val id: String,
    val trx: ExecutedTransactionParent,
    val block_time: Date,
    val block_num: Int,
    val last_irreversible_block: Int,
    val traces: List<TransactionActionTrace>
)