import com.squareup.moshi.Moshi
import io.golos.commun4j.BuildConfig
import io.golos.commun4j.http.rpc.RpcServerMessage
import io.golos.commun4j.http.rpc.RpcServerMessageCallback
import io.golos.commun4j.http.rpc.SocketClientImpl
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class SocketTest {

    @Test
    fun reconnectTest() {
        val impl = SocketClientImpl(BuildConfig.SERVICES_DEV, Moshi.Builder().build(), object : RpcServerMessageCallback {
            override fun onMessage(message: RpcServerMessage) {

            }
        })
        val latch = CountDownLatch(1)

        var answerReceived = false

        Thread(Runnable {
            impl.send("test", "test", Any::class.java)
            impl.dropConnection()

            try {
                impl.send("test", "test", Any::class.java)
            } catch (e: Exception) {
                impl.send("test", "test", Any::class.java)
            }

            answerReceived = true
            latch.countDown()
        }).run()

        latch.await(30, TimeUnit.SECONDS)
        Assert.assertTrue("reconnection doesnt work", answerReceived)
    }
}