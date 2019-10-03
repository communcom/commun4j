/**
 * Copyright 2013-present memtrip LTD.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http//www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.golos.commun4j.abi.writer

import io.golos.commun4j.core.crypto.EosPublicKey
import io.golos.commun4j.sharedmodel.CheckSum256
import io.golos.commun4j.sharedmodel.CyberAsset
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbol
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import io.golos.commun4j.sharedmodel.CyberTimeStampMicroseconds
import io.golos.commun4j.sharedmodel.CyberTimeStampSeconds
import io.golos.commun4j.sharedmodel.ISquishable
import io.golos.commun4j.sharedmodel.Varuint

interface ByteWriter {
    fun putName(value: String?, isNullable: Boolean)

    fun putName(value: CyberName?, isNullable: Boolean)

    fun putAccountName(value: String?, isNullable: Boolean)

    fun putBlockNum(value: Int?, isNullable: Boolean)

    fun putBlockPrefix(value: Long?, isNullable: Boolean)

    fun putPublicKey(value: EosPublicKey?, isNullable: Boolean)

    fun putPublicKey(value: String?, isNullable: Boolean)

    fun putAsset(value: String?, isNullable: Boolean)

    fun putAsset(value: CyberAsset?, isNullable: Boolean)

    fun putChainId(value: String?, isNullable: Boolean)

    fun putData(value: String?, isNullable: Boolean)

    fun putCheckSum(value: CheckSum256?, isNullable: Boolean)

    fun putTimestampMs(value: Long?, isNullable: Boolean)

    fun putTimestampMs(value: CyberTimeStampSeconds?, isNullable: Boolean)

    fun putBoolean(value: Boolean?, isNullable: Boolean)

    fun putShort(value: Short?, isNullable: Boolean)

    fun putInt(value: Int?, isNullable: Boolean)

    fun putVariableUInt(value: Long?, isNullable: Boolean)

    fun putVariableUInt(value: Int?, isNullable: Boolean)

    fun putVariableUInt(value: Varuint?, isNullable: Boolean)

    fun putLong(value: Long?, isNullable: Boolean)

    fun putLong(value: CyberTimeStampMicroseconds?, isNullable: Boolean)

    fun putFloat(value: Float?, isNullable: Boolean)

    fun putBytes(value: ByteArray?, isNullable: Boolean)

    fun putString(value: String?, isNullable: Boolean)

    fun putByte(value: Byte?, isNullable: Boolean)

    fun putStringCollection(value: List<String>?, isNullable: Boolean)

    fun putShortCollection(value: List<Short>?, isNullable: Boolean)

    fun putIntCollection(value: List<Int>?, isNullable: Boolean)

    fun putLongCollection(value: List<Long>?, isNullable: Boolean)

    fun putHexCollection(value: List<String>?, isNullable: Boolean)

    fun putAccountNameCollection(value: List<String>?, isNullable: Boolean)

    fun putCyberNamesCollection(value: List<CyberName>?, isNullable: Boolean)

    fun putSymbolCode(value: CyberSymbolCode?, isNullable: Boolean)

    fun putSymbol(value: CyberSymbol?, isNullable: Boolean)

    fun putInterfaceCollection(value: List<ISquishable>?, isNullable: Boolean)

    fun toBytes(): ByteArray

    fun length(): Int
}