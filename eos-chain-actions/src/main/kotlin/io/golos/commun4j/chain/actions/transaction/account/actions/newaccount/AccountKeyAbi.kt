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
package io.golos.commun4j.chain.actions.transaction.account.actions.newaccount

import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.PublicKeyCompress
import io.golos.commun4j.abi.writer.ShortCompress

@Abi
data class AccountKeyAbi(
    val key: String,
    val weight: Short
) {

    val getKey: String
        @PublicKeyCompress get() = key

    val getWeight: Short
        @ShortCompress get() = weight
}
