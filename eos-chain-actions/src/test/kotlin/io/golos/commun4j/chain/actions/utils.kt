package io.golos.commun4j.chain.actions

import java.util.Date
import java.util.Random
import java.util.Calendar
import kotlin.streams.asSequence

fun generateUniqueAccountName(): String {
    val source = "abcdefghijklmnopqrstuvwxyz"
    return Random().ints(12, 0, source.length)
            .asSequence()
            .map(source::get)
            .joinToString("")
}

class Config {
    companion object {
        const val CHAIN_API_BASE_URL = "https://api.jungle.alohaeos.com:443/"
        const val MAINNET_API_BASE_URL = "https://eos.greymass.com/"
    }
}

fun transactionDefaultExpiry(): Date = with(Calendar.getInstance()) {
    set(Calendar.MINUTE, get(Calendar.MINUTE) + 2)
    this
}.time