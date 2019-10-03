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
package io.golos.commun4j.abi.writer.bytewriter

import io.golos.commun4j.abi.writer.ByteWriter
import io.golos.commun4j.core.crypto.EosPublicKey
import io.golos.commun4j.core.hex.DefaultHexWriter
import io.golos.commun4j.core.hex.HexWriter
import io.golos.commun4j.sharedmodel.*

class DefaultByteWriter(
        capacity: Int
) : ByteWriter {

    private val nameWriter: NameWriter = NameWriter()
    private val accountNameWriter: AccountNameWriter = AccountNameWriter()
    private val publicKeyWriter: PublicKeyWriter = PublicKeyWriter()
    private val hexWriter: HexWriter = DefaultHexWriter()
    private val assetWriter: AssetWriter = AssetWriter()
    private val chainIdWriter: ChainIdWriter = ChainIdWriter()
    private val hexCollectionWriter: HexCollectionWriter = HexCollectionWriter()

    private val buffer = ByteArrayBuffer(capacity)

    override fun putName(value: String?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { name -> nameWriter.put(name, this) })
        else nameWriter.put(value!!, this)
    }


    override fun putName(value: CyberName?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { name -> nameWriter.put(name.name, this) })
        else nameWriter.put(value!!.name, this)
    }


    override fun putAccountName(value: String?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { name -> accountNameWriter.put(name, this) })
        else accountNameWriter.put(value!!, this)
    }

    override fun putBlockNum(value: Int?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { blockNum -> buffer.append((blockNum and 0xFFFF).toShort()) })
        else buffer.append((value!! and 0xFFFF).toShort())
    }

    override fun putBlockPrefix(value: Long?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { prefix -> putInt((prefix and -0x1).toInt(), false) })
        else putInt((value!! and -0x1).toInt(), false)
    }

    override fun putPublicKey(value: EosPublicKey?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { key -> publicKeyWriter.put(key, this) })
        else publicKeyWriter.put(value!!, this)
    }

    override fun putPublicKey(value: String?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { key -> publicKeyWriter.put(EosPublicKey(key), this) })
        else publicKeyWriter.put(EosPublicKey(value!!), this)
    }

    override fun putAsset(value: String?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { asset -> assetWriter.put(asset, this) })
        else assetWriter.put(value!!, this)
    }

    override fun putAsset(value: CyberAsset?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { asset -> assetWriter.put(asset.amount, this) })
        else assetWriter.put(value!!.amount, this)
    }


    override fun putSymbolCode(value: CyberSymbolCode?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { symbolCode -> putBytes(symbolCode.symbolCode, false) })
        else putBytes(value!!.symbolCode, false)
    }

    override fun putSymbol(value: CyberSymbol?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { symbol -> putBytes(symbol.symbolCode, false) })
        else putBytes(value!!.symbolCode, false)
    }

    override fun putChainId(value: String?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { chainId -> chainIdWriter.put(chainId, this) })
        else chainIdWriter.put(value!!, this)
    }

    override fun putData(value: String?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { data ->
            val dataAsBytes = hexWriter.hexToBytes(data)
            putVariableUInt(dataAsBytes.size.toLong(), false)
            putBytes(dataAsBytes, false)
        })
        else {
            val dataAsBytes = hexWriter.hexToBytes(value!!)
            putVariableUInt(dataAsBytes.size.toLong(), false)
            putBytes(dataAsBytes, false)
        }
    }

    override fun putCheckSum(value: CheckSum256?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { checkSum -> putBytes(checkSum.value, false) })
        else putBytes(value!!.value, false)
    }

    override fun putTimestampMs(value: Long?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { timestampMs -> putInt((timestampMs / 1000).toInt(), false) })
        else putInt((value!! / 1000).toInt(), false)
    }

    override fun putTimestampMs(value: CyberTimeStampSeconds?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { timestampMs -> putTimestampMs(timestampMs.value, false) })
        else putTimestampMs(value!!.value, false)
    }

    override fun putBoolean(value: Boolean?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { bool -> buffer.append(if (bool) (1).toByte() else (0).toByte()) })
        else buffer.append(if (value!!) (1).toByte() else (0).toByte())
    }


    override fun putShort(value: Short?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { short -> buffer.append(short) })
        else buffer.append(value!!)
    }

    override fun putInt(value: Int?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { int -> buffer.append(int) })
        else buffer.append(value!!)
    }

    override fun putVariableUInt(value: Long?, isNullable: Boolean) {

        if (isNullable) putNullable(value, { varuint ->
            var v: Long = varuint
            while (v >= 0x80) {
                val b = ((v and 0x7f) or 0x80).toByte()
                buffer.append(b)
                v = v ushr 7
            }
            buffer.append(v.toByte())

        }) else {
            var v: Long = value!!
            while (v >= 0x80) {
                val b = ((v and 0x7f) or 0x80).toByte()
                buffer.append(b)
                v = v ushr 7
            }
            buffer.append(v.toByte())
        }
    }

    override fun putVariableUInt(value: Int?, isNullable: Boolean) {
        putVariableUInt(value?.toLong(), isNullable)
    }

    override fun putVariableUInt(value: Varuint?, isNullable: Boolean) {
        if (isNullable) putNullable(value, { varuint -> putVariableUInt(varuint.value, false) })
        else putVariableUInt(value!!.value, false)
    }

    override fun putLong(value: Long?, isNullable: Boolean) {
        if (isNullable) putNullable(value) { long -> buffer.append(long) }
        else buffer.append(value!!)
    }

    override fun putLong(value: CyberTimeStampMicroseconds?, isNullable: Boolean) {
        if (isNullable) putNullable(value) { cyberTimestampMiscrosends -> buffer.append(cyberTimestampMiscrosends.value) }
        else buffer.append(value!!.value)
    }

    override fun putFloat(value: Float?, isNullable: Boolean) {
        if (isNullable) putNullable(value) { float -> buffer.append(float) }
        else buffer.append(value!!)
    }

    override fun putBytes(value: ByteArray?, isNullable: Boolean) {
        if (isNullable) putNullable(value) { byteArray -> buffer.append(byteArray) }
        else buffer.append(value!!)
    }

    override fun putByte(value: Byte?, isNullable: Boolean) {
        if (isNullable) putNullable(value) { byte -> buffer.append(byte) }
        else buffer.append(value!!)
    }

    override fun putString(value: String?, isNullable: Boolean) {
        if (isNullable) putNullable(value) { string ->
            val bytes = string.toByteArray()
            putVariableUInt(bytes.size.toLong(), false)
            buffer.append(bytes)
        } else {
            val bytes = value!!.toByteArray()
            putVariableUInt(bytes.size.toLong(), false)
            buffer.append(bytes)
        }
    }


    override fun putLongCollection(value: List<Long>?, isNullable: Boolean) {
        if (isNullable) {
            putNullable(value) { notNullLongList ->
                putVariableUInt(notNullLongList.size.toLong(), false)
                if (notNullLongList.isNotEmpty()) {
                    for (long in notNullLongList) {
                        putLong(long, false)
                    }
                }
            }
        } else {
            putVariableUInt(value!!.size.toLong(), false)
            if (value.isNotEmpty()) {
                for (long in value) {
                    putLong(long, false)
                }
            }
        }
    }

    override fun putShortCollection(value: List<Short>?, isNullable: Boolean) {
        if (isNullable) {
            putNullable(value) { notNullShortList ->
                putVariableUInt(notNullShortList.size.toLong(), false)
                if (notNullShortList.isNotEmpty()) {
                    for (long in notNullShortList) {
                        putShort(long, false)
                    }
                }
            }
        } else {
            putVariableUInt(value!!.size.toLong(), false)
            if (value.isNotEmpty()) {
                for (long in value) {
                    putShort(long, false)
                }
            }
        }
    }

    override fun putIntCollection(value: List<Int>?, isNullable: Boolean) {
        if (isNullable) {
            putNullable(value) { notNullIntList ->
                putVariableUInt(notNullIntList.size.toLong(), false)
                if (notNullIntList.isNotEmpty()) {
                    for (int in notNullIntList) {
                        putInt(int, false)
                    }
                }
            }
        } else {
            putVariableUInt(value!!.size.toLong(), false)
            if (value.isNotEmpty()) {
                for (int in value) {
                    putInt(int, false)
                }
            }
        }
    }

    override fun putStringCollection(value: List<String>?, isNullable: Boolean) {
        if (isNullable) {
            putNullable(value) { stringList ->
                putVariableUInt(stringList.size.toLong(), false)
                if (stringList.isNotEmpty()) {
                    for (string in stringList) {
                        putString(string, false)
                    }
                }
            }
        } else {
            putVariableUInt(value!!.size.toLong(), false)
            if (value.isNotEmpty()) {
                for (string in value) {
                    putString(string, false)
                }
            }
        }
    }

    override fun putHexCollection(value: List<String>?, isNullable: Boolean) {
        if (isNullable) {
            putNullable(value) { notNullHexes ->
                hexCollectionWriter.put(notNullHexes, this)
            }
        } else {
            hexCollectionWriter.put(value!!, this)
        }
    }

    override fun putAccountNameCollection(value: List<String>?, isNullable: Boolean) {
        if (isNullable) {
            putNullable(value) { accountNamesList ->
                putVariableUInt(accountNamesList.size.toLong(), false)
                if (accountNamesList.isNotEmpty()) {
                    for (accountName in accountNamesList) {
                        putAccountName(accountName, false)
                    }
                }
            }
        } else {
            putVariableUInt(value!!.size.toLong(), false)
            if (value.isNotEmpty()) {
                for (accountName in value) {
                    putAccountName(accountName, false)
                }
            }
        }
    }

    override fun putInterfaceCollection(value: List<ISquishable>?, isNullable: Boolean) {
        if (isNullable) {
            putNullable(value) { notNullCollection ->
                putVariableUInt(notNullCollection.size.toLong(), false)
                notNullCollection.forEach {
                    putVariableUInt(it.getStructIndexForCollectionSquish().toLong(), false)
                    putBytes(it.squish(), false)
                }
            }
        } else {
            putVariableUInt(value!!.size.toLong(), false)
            value.forEach {
                putVariableUInt(it.getStructIndexForCollectionSquish().toLong(), false)
                putBytes(it.squish(), false)
            }
        }
    }

    override fun putCyberNamesCollection(value: List<CyberName>?, isNullable: Boolean) {
        putAccountNameCollection(value?.map { it.name }, false)
    }

    override fun toBytes(): ByteArray = buffer.toByteArray()

    override fun length(): Int = buffer.length()

    private inline fun <T> putNullable(value: T?,
                                       notNullAction: ByteWriter.(value: T) -> Unit) {

        putBoolean(value != null, false)
        if (value != null) this.notNullAction(value)
    }
}