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
package io.golos.commun4j.core.base58

import io.golos.commun4j.core.hash.RIPEMD160Digest

import org.bitcoinj.core.Base58

class Base58Encode {

    fun encodeSignature(prefix: String, data: ByteArray): String {
        return encodeWithChecksum(prefix, "K1", data)
    }

    fun encodeKey(prefix: String, data: ByteArray): String {
        return encodeWithChecksum(prefix, "", data)
    }

    private fun encodeWithChecksum(prefix: String, signaturePrefix: String, data: ByteArray): String {

        val dataWithChecksum = ByteArray(data.size + 4)

        System.arraycopy(data, 0, dataWithChecksum, 0, data.size)
        System.arraycopy(encodeChecksum(data, signaturePrefix), 0, dataWithChecksum, data.size, 4)

        return if (signaturePrefix.isEmpty()) {
            prefix + Base58.encode(dataWithChecksum)
        } else {
            prefix + "_" + signaturePrefix + "_" + Base58.encode(dataWithChecksum)
        }
    }

    private fun encodeChecksum(data: ByteArray, vararg extras: String): ByteArray {

        val toHashData = ByteArray(data.size + extras.sumBy { it.length })

        System.arraycopy(data, 0, toHashData, 0, data.size)

        extras.filter { extra ->
            extra.isNotEmpty()
        }.forEach { extra ->
            System.arraycopy(extra.toByteArray(), 0, toHashData, data.size, extra.length)
        }

        return RIPEMD160Digest.hash(toHashData)
    }
}