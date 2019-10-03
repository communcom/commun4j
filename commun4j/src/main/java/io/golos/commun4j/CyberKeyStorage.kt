package io.golos.commun4j

import io.golos.commun4j.core.crypto.EosPrivateKey
import io.golos.commun4j.model.AuthType
import io.golos.commun4j.utils.Pair
import io.golos.commun4j.sharedmodel.CyberName
import java.util.*
import kotlin.collections.HashMap

class KeyStorage {

    private var activeAccount: CyberName? = null
    private val accounts = Collections.synchronizedMap(HashMap<CyberName, Set<Pair<AuthType, String>>>())

    @Synchronized
    fun getActiveAccountKeys(): Set<Pair<AuthType, String>> {
        val activeAcc = activeAccount
                ?: throw java.lang.IllegalStateException("active account not set")
        return accounts[activeAcc]!!
    }

    @Synchronized
    fun getActiveAccount(): CyberName {
        return activeAccount
                ?: throw java.lang.IllegalStateException("active account not set")
    }

    @Synchronized
    fun isActiveAccountSet() = activeAccount != null

    fun getAccountKeys(accName: CyberName) = accounts[accName]

    fun addAccountKeys(accName: CyberName, keys: Set<Pair<AuthType, String>>) {
        keys.forEach {
            EosPrivateKey(it.second)
        }
        synchronized(this) {
            val oldKeys = accounts[accName]
            val resultingKeys = keys + oldKeys.orEmpty()
            accounts[accName] = resultingKeys
            setAccountActive(accName)
        }
    }

    @Synchronized
    fun setAccountActive(name: CyberName) {
        accounts[name].takeIf { it == null }?.let { throw IllegalStateException("no keys for $name account name") }
        activeAccount = name
    }
}