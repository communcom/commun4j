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
package io.golos.commun4j.chain.actions

data class ChainResponse<T> (
    val isSuccessful: Boolean,
    val statusCode: Int,
    val body: T?,
    val errorBody: io.golos.commun4j.chain.actions.ChainError?
) {

    companion object {
        fun <T> error(): io.golos.commun4j.chain.actions.ChainResponse<T> {
            return io.golos.commun4j.chain.actions.ChainResponse(
                    false,
                    400,
                    null,
                    null)
        }
    }
}

data class ChainError(
    val code: Long,
    val message: String,
    val error: io.golos.commun4j.chain.actions.Error
)

data class Error(
    val code: Long,
    val name: String,
    val what: String,
    val details: List<io.golos.commun4j.chain.actions.Details>
)

data class Details(
    val message: String,
    val method: String
)