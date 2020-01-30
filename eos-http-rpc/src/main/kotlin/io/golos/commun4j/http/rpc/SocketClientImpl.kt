package io.golos.commun4j.http.rpc

import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.golos.commun4j.http.rpc.model.ApiResponseError
import io.golos.commun4j.sharedmodel.Either
import io.golos.commun4j.sharedmodel.LogLevel
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.net.SocketTimeoutException
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

interface RpcServerMessageCallback {
    fun onMessage(message: RpcServerMessage)
}

class SocketClientImpl(
        private val socketUrl: String,
        private val moshi: Moshi,
        private val rpcServerCallback: RpcServerMessageCallback,
        private val readTimeoutInSeconds: Int = 30,
        private val logLevel: LogLevel? = null,
        private val logger: HttpLoggingInterceptor.Logger? = null
) : WebSocketListener(), SocketClient {
    private lateinit var webSocket: WebSocket
    private val latches = Collections.synchronizedMap<Long, CountDownLatch>(hashMapOf())
    private val responseMap = Collections.synchronizedMap<Long, String?>(hashMapOf())
    private val isSocketConnected = AtomicBoolean(false)
    private var callbackExecutor: ExecutorService? = null

    private fun connect() {
        synchronized(this) {
            if (isSocketConnected.get()) return
            if (callbackExecutor?.isShutdown != false) callbackExecutor = Executors.newSingleThreadExecutor()

            webSocket = OkHttpClient
                    .Builder()
                    .addInterceptor(HttpLoggingInterceptor().also { it.level = HttpLoggingInterceptor.Level.BODY })
                    .connectionPool(SharedConnectionPool.pool)
                    .build()
                    .newWebSocket(Request.Builder()
                            .url(socketUrl)
                            .build(), this)
        }
    }

    override fun <R> send(
            method: String,
            params: Any,
            classOfMessageToReceive: Class<R>
    ): Either<R, ApiResponseError> {

        connect()

        val rpcMessage = RpcMessageWrapper(method, params)

        val stringToSend = moshi.adapter(RpcMessageWrapper::class.java).toJson(rpcMessage)

        log("sending $stringToSend")
        webSocket.send(stringToSend)

        val id = rpcMessage.id

        latches[id] = CountDownLatch(1)
        latches[id]!!.await(readTimeoutInSeconds.toLong(), TimeUnit.SECONDS)

        val response = responseMap[id]
                ?: throw SocketTimeoutException(
                        "socket was unable to answer " +
                                "for request $stringToSend"
                )
        responseMap[id] = null
        latches[id] = null

        val type = Types.newParameterizedType(RpcResponseWrapper::class.java, classOfMessageToReceive)

        return try {
            val error = moshi.adapter(ApiResponseError::class.java).nonNull().fromJson(response)!!
            Either.Failure(error)
        } catch (e: JsonDataException) {
            val responseWrapper = moshi.adapter<RpcResponseWrapper<R>>(type).nonNull().fromJson(response)!!
            Either.Success(responseWrapper.result)
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        System.err.println("socket failure $response")
        isSocketConnected.set(false)
        t.printStackTrace()
        latches.forEach(action = {
            it.value?.countDown()
        })
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        System.err.println("socket is closing by $reason with code $code")
        super.onClosing(webSocket, code, reason)
        latches.forEach(action = {
            it.value?.countDown()
        })
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        isSocketConnected.set(true)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        isSocketConnected.set(false)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        log("onMessage $text")
        try {
            val id = moshi.adapter(Identifiable::class.java).nullSafe().fromJson(text)?.id
            if (id != null) {
                responseMap[id] = text
                latches[id]?.countDown()
            }
        } catch (e: Exception) {//incoming message
            callbackExecutor?.execute {
                try {
                    val message = moshi.adapter<RpcServerMessage>(RpcServerMessage::class.java).nonNull().fromJson(text)!!
                    rpcServerCallback.onMessage(message)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    override fun dropConnection() {
        synchronized(this) {
            if (::webSocket.isInitialized) webSocket.close(1000, "connection drop by user request")
            callbackExecutor?.shutdown()
            callbackExecutor = null
        }
    }

    private fun log(str: String) {
        if (logLevel != LogLevel.BODY) return
        logger?.log(str)
        if (logger == null) println(str)
    }

}

@JsonClass(generateAdapter = true)
internal class RpcResponseWrapper<T>(val id: Long, val result: T)

@JsonClass(generateAdapter = true)
internal class Identifiable(val id: Long)

@JsonClass(generateAdapter = true)
data class RpcServerMessage(val method: String, val params: Any)


@JsonClass(generateAdapter = true)
class RpcMessageWrapper(val method: String,
                        val params: Any,
                        val jsonrpc: String = "2.0",
                        val id: Long = requestCounter.incrementAndGet())

private val requestCounter = AtomicLong(0)