import io.golos.commun4j.http.rpc.RpcServerMessage
import io.golos.commun4j.http.rpc.RpcServerMessageCallback
import org.junit.Assert
import org.junit.Test

class ServerRpcPushTest {

    @Test
    fun testGetAuth() {
        var lastMessage: RpcServerMessage? = null
        val client = getClient(CONFIG_TYPE.DEV, true, true, object : RpcServerMessageCallback {
            override fun onMessage(message: RpcServerMessage) {
                println(message)
                lastMessage = message
            }
        })
        Assert.assertNotNull(lastMessage)
    }

    @Test
    fun testSubAndUnSubOnNotifs() {
        val client = getClient(setActiveUser = true, authInServices = true)
        client.subscribeOnNotifications().getOrThrow()
        client.unSubscribeFromNotifications().getOrThrow()
    }
}