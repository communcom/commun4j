package io.golos.commun4j.sharedmodel

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.math.BigInteger

class CyberNameAdapter : JsonAdapter<CyberName>() {

    override fun fromJson(reader: JsonReader): CyberName? {
        val nextToken = reader.peek()
        return if (nextToken == JsonReader.Token.STRING) {
            val value = reader.nextString()
            CyberName(value)
        } else {
            reader.beginObject()
            reader.nextName()
            val out = CyberName(reader.nextString())
            reader.endObject()
            out
        }
    }

    override fun toJson(writer: JsonWriter, value: CyberName?) {
        writer.value(value?.name)
    }
}

class BigIntegerAdapter : JsonAdapter<BigInteger>() {

    override fun fromJson(reader: JsonReader): BigInteger? {
        val value = reader.nextString()
        return BigInteger(value)
    }

    override fun toJson(writer: JsonWriter, value: BigInteger?) {
        writer.value(value?.toString() ?: "")
    }
}

class CyberAssetAdapter : JsonAdapter<CyberAsset>() {

    override fun fromJson(reader: JsonReader): CyberAsset? {
        val nextToken = reader.peek()
        return if (nextToken == JsonReader.Token.STRING) {
            val value = reader.nextString()
            CyberAsset(value)
        } else {
            val token = reader.peek()
            if (token == JsonReader.Token.NULL) {
                return reader.nextNull<CyberAsset>()
            }

            reader.beginObject()
            reader.nextName()
            val out = CyberAsset(reader.nextString())
            reader.endObject()
            out
        }
    }

    override fun toJson(writer: JsonWriter, value: CyberAsset?) {
        writer.value(value?.amount)
    }
}

class CyberSymbolCodeAdapter : JsonAdapter<CyberSymbolCode>() {

    override fun fromJson(reader: JsonReader): CyberSymbolCode? {
        val nextToken = reader.nextString() ?: return null
        return CyberSymbolCode(nextToken)
    }

    override fun toJson(writer: JsonWriter, value: CyberSymbolCode?) {
        writer.value(value?.value)
    }
}

class CyberSymbolAdapter : JsonAdapter<CyberSymbol>() {

    override fun fromJson(reader: JsonReader): CyberSymbol? {
        val nextToken = reader.peek()
        return if (nextToken == JsonReader.Token.STRING) {
            val value = reader.nextString()
            CyberSymbol(value)
        } else {
            reader.beginObject()
            reader.nextName()
            val out = CyberSymbol(reader.nextString())
            reader.endObject()
            out
        }
    }

    override fun toJson(writer: JsonWriter, value: CyberSymbol?) {
        writer.value(value?.symbol)
    }
}

class CyberTimeStampAdapter : JsonAdapter<CyberTimeStampSeconds>() {

    override fun fromJson(reader: JsonReader): CyberTimeStampSeconds? {
        val nextToken = reader.peek()
        if (nextToken == JsonReader.Token.NULL) {
            reader.nextNull<CyberTimeStampSeconds>()
            return null
        }
        reader.beginObject()
        reader.nextName()
        val out = CyberTimeStampSeconds(reader.nextLong())
        reader.endObject()
        return out
    }

    override fun toJson(writer: JsonWriter, value: CyberTimeStampSeconds?) {
        writer.value(value?.value)
    }
}

class CyberTimeStampMsAdapter : JsonAdapter<CyberTimeStampMicroseconds>() {

    override fun fromJson(reader: JsonReader): CyberTimeStampMicroseconds? {
        val nextToken = reader.peek()
        if (nextToken == JsonReader.Token.NULL) {
            reader.nextNull<CyberTimeStampMicroseconds>()
            return null
        }
        reader.beginObject()
        reader.nextName()
        val out = CyberTimeStampMicroseconds(reader.nextLong())
        reader.endObject()
        return out
    }

    override fun toJson(writer: JsonWriter, value: CyberTimeStampMicroseconds?) {
        writer.value(value?.value)
    }
}

class VariableUintAdapter : JsonAdapter<Varuint>() {

    override fun fromJson(reader: JsonReader): Varuint? {
        val nextToken = reader.peek()
        if (nextToken == JsonReader.Token.NULL) {
            reader.nextNull<Varuint>()
            return null
        }
        reader.beginObject()
        reader.nextName()
        val out = Varuint(reader.nextLong())
        reader.endObject()
        return out
    }

    override fun toJson(writer: JsonWriter, value: Varuint?) {
        writer.value(value?.value)
    }
}

