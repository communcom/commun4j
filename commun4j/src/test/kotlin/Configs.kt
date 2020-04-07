import io.golos.commun4j.BuildConfig
import io.golos.commun4j.Commun4j
import io.golos.commun4j.http.rpc.RpcServerMessage
import io.golos.commun4j.http.rpc.RpcServerMessageCallback
import io.golos.commun4j.model.AuthType
import io.golos.commun4j.sharedmodel.Commun4jConfig
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.Either
import io.golos.commun4j.sharedmodel.SocketOpenQueryParams
import io.golos.commun4j.utils.StringSigner
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap


enum class CONFIG_TYPE {
    DEV
}

private val devConfig = Commun4jConfig(blockChainHttpApiUrl = BuildConfig.BLOCKCHAIN_DEV,
        servicesUrl = BuildConfig.SERVICES_DEV, socketOpenQueryParams = SocketOpenQueryParams("1.0.0", deviceId = UUID.randomUUID().toString()))


fun CONFIG_TYPE.toConfig() = when (this) {
    CONFIG_TYPE.DEV -> devConfig
}

fun Commun4jConfig.toConfigType() = when (this) {
    devConfig -> CONFIG_TYPE.DEV
    else -> throw IllegalArgumentException("unknown config type")
}

private fun CyberName.checkAccount(forConfig: CONFIG_TYPE): Boolean {
    val acc = Commun4j(forConfig.toConfig()).getUserAccount(this)
    return acc is Either.Success
}

private const val delimiter = "###"

private fun getAccount(sourceFile: File,
                       configType: CONFIG_TYPE): Pair<CyberName, String> {
    val out: Pair<CyberName, String>

    out = if (!sourceFile.exists()) {
        AccountCreationTest.createNewAccount(configType.toConfig())
    } else {
        val contents = sourceFile.readText().split(delimiter)

        if (contents.isEmpty() || contents.size != 2) AccountCreationTest.createNewAccount(configType.toConfig())

        val cyberName = CyberName(contents[0])
        val key = contents[1]
        if (!cyberName.checkAccount(configType)) {
            AccountCreationTest.createNewAccount(configType.toConfig())
        } else cyberName to key
    }
    sourceFile.writeText("${out.first.name}$delimiter${out.second}")
    return out
}

private fun firstAccount(forConfig: CONFIG_TYPE): Pair<CyberName, String> {
    return getAccount(File(File(".").canonicalPath, "/first_acc_$forConfig.txt"), forConfig)
}

private val savedAccs = ConcurrentHashMap<CONFIG_TYPE, Pair<CyberName, String>>()

@Synchronized
fun account(forConfig: CONFIG_TYPE,
            createNew: Boolean = false): Pair<CyberName, String> {
    return if (createNew) AccountCreationTest.createNewAccount(forConfig.toConfig())
    else savedAccs.getOrPut(forConfig) {
        getAccount(File(File(".").canonicalPath,
                "/second_acc_$forConfig.txt"), forConfig)
    }
}


@Synchronized
fun getClient(ofType: CONFIG_TYPE = CONFIG_TYPE.DEV,
              setActiveUser: Boolean = true,
              authInServices: Boolean = false,
              serverMessageCallback: RpcServerMessageCallback = object : RpcServerMessageCallback{
                  override fun onMessage(message: RpcServerMessage) {
                      println(message)
                  }
              }): Commun4j {
    return Commun4j(config = ofType.toConfig(), serverMessageCallback = serverMessageCallback)
            .apply {
                if (setActiveUser) {
                    val account = firstAccount(ofType)
                    keyStorage.addAccountKeys(account.first,
                            setOf(io.golos.commun4j.utils.Pair(AuthType.ACTIVE, account.second)))
                }

                if (authInServices) {
                    val secret = getAuthSecret().getOrThrow().secret

                    val profile = getUserProfile(activeAccountPair.first, null).getOrThrow()

                    val authResult = authWithSecret(profile.username!!,
                            secret,
                            StringSigner.signString(secret,
                                    activeAccountPair.second))
                    org.junit.Assert.assertTrue(authResult is Either.Success)
                }
            }
}


