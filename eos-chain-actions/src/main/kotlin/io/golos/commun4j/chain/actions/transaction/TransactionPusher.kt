package io.golos.commun4j.chain.actions.transaction

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.ITransactionPusher
import io.golos.commun4j.chain.actions.SignedTransactionProvider
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.SignedTransactionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAbi
import io.golos.commun4j.core.block.BlockIdDetails
import io.golos.commun4j.core.crypto.EosPrivateKey
import io.golos.commun4j.core.crypto.signature.PrivateKeySigning
import io.golos.commun4j.http.rpc.model.info.Info
import io.golos.commun4j.http.rpc.model.signing.PushTransaction
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.sharedmodel.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.lang.ref.WeakReference
import java.math.BigInteger
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object TransactionPusher: ITransactionPusher, SignedTransactionProvider {
    private val moshi: Moshi = Moshi
            .Builder()
            .add(CyberName::class.java, CyberNameAdapter())
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .add(CyberAsset::class.java, CyberAssetAdapter())
            .add(CyberSymbol::class.java, CyberSymbolAdapter())
            .add(CyberTimeStampSeconds::class.java, CyberTimeStampAdapter())
            .add(Varuint::class.java, VariableUintAdapter())
            .add(CyberTimeStampMsAdapter::class.java, CyberTimeStampMsAdapter())
            .add(BigInteger::class.java, BigIntegerAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()

    private val interceptor = io.golos.commun4j.chain.actions.MyHttpInterceptor()

    private var okHttpClient = OkHttpClient
            .Builder()
            .dispatcher(Dispatcher(Executors.newSingleThreadExecutor { Thread.currentThread() }))
            .readTimeout(30L, TimeUnit.SECONDS)
            .writeTimeout(30L, TimeUnit.SECONDS)
            .connectTimeout(30L, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .connectionPool(io.golos.commun4j.http.rpc.SharedConnectionPool.pool)
            .build()

    override fun <T : Any> push(actions: List<ActionAbi>, keys: List<EosPrivateKey>, traceType: Class<T>, blockChainUrl: String, logLevel: LogLevel?, logger: HttpLoggingInterceptor.Logger?): Either<TransactionCommitted<T>, GolosEosError> {
       return pushTransaction(actions, keys.toSet(), traceType, blockChainUrl, logLevel, logger)
    }

    @JvmOverloads
    fun <T : Any> pushTransaction(actions: List<ActionAbi>,
                                  keys: Set<EosPrivateKey>,
                                  traceType: Class<T>,
                                  blockChainUrl: String,
                                  logLevel: LogLevel? = null,
                                  logger: HttpLoggingInterceptor.Logger? = null): Either<TransactionCommitted<T>, GolosEosError> {

        val signatureResult =
                createSignedTransaction(actions, keys.toList(), blockChainUrl, logLevel, logger)

        if (signatureResult is Either.Failure) return Either.Failure(signatureResult.value)

        signatureResult as Either.Success

        val result = okHttpClient.newCall(Request.Builder()
                .post(RequestBody.create(MediaType.parse("application/json"),
                        moshi.adapter(PushTransaction::class.java).toJson(PushTransaction
                        (
                                signatureResult.value.signedTransactionSignatures,
                                "none",
                                "",
                                AbiBinaryGenTransactionWriter(CompressionType.NONE)
                                        .squishTransactionAbi(signatureResult.value.transaction, false).toHex()
                        ))))
                .url("${blockChainUrl.removeSuffix("/")}/v1/chain/push_transaction")
                .build()
                .apply {
                    if (logger != null)
                        interceptor.loggers[this] = WeakReference(logger) to logLevel.adapt()
                })
                .execute()


        return if (result.isSuccessful) {
            val response = result.body()!!.string()

            return try {

                try {
                    val type = Types.newParameterizedType(TransactionCommitted::class.java, traceType)
                    val value = moshi
                            .adapter<TransactionCommitted<T>>(type)
                            .fromJson(response)!!

                    Either.Success(value.copy(
                            resolvedResponse = value.processed.action_traces.map {
                                try {
                                    moshi.adapter<T>(traceType).fromJsonValue(it.act.data)
                                } catch (ignored: JsonDataException) {
                                    null
                                }
                            }.filterNotNull()
                                    .firstOrNull()
                    ))
                } catch (e: RuntimeException) {
                    val type = Types.newParameterizedType(TransactionCommitted::class.java, Any::class.java)
                    val value = moshi
                            .adapter<TransactionCommitted<T>>(type)
                            .fromJson(response)!!

                    Either.Success(value)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Either.Failure(moshi.adapter(GolosEosError::class.java).fromJson(response)!!)
            }
        } else {
            Either.Failure(moshi.adapter(GolosEosError::class.java).fromJson(result.body()!!.string().orEmpty())!!)
        }
    }

   override fun createSignedTransaction(
            action: List<ActionAbi>,
            keys: List<EosPrivateKey>,
            blockChainUrl: String,
            logLevel: LogLevel?,
            logger: HttpLoggingInterceptor.Logger?): Either<SignedTransactionResult, GolosEosError> {

        val infoResp = getInfo(okHttpClient, blockChainUrl, logLevel, logger)

        if (infoResp is Either.Failure) return Either.Failure(infoResp.value)

        val info = (infoResp as Either.Success).value

        val transaction = createTransaction(BlockIdDetails(info.head_block_id), action)

        return Either.Success(SignedTransactionResult(transaction, keys.map { key ->
            signTransactionAndGetSignature(info, transaction, key, logLevel, logger)
        }, infoResp.value))
    }


    private fun getInfo(client: OkHttpClient,
                        blockChainUrl: String,
                        logLevel: LogLevel?,
                        logger: HttpLoggingInterceptor.Logger?): Either<Info, GolosEosError> {
        val resp = client.newCall(Request.Builder()
                .post(RequestBody.create(MediaType.parse("application/json"), ""))
                .url("${blockChainUrl.removeSuffix("/")}/v1/chain/get_info")
                .build()
                .apply {
                    if (logger != null)
                        interceptor.loggers[this] = WeakReference(logger) to logLevel.adapt()
                })
                .execute()

        if (!resp.isSuccessful) {
            return Either.Failure(moshi.adapter(GolosEosError::class.java).fromJson(resp.body()?.string()
                    ?: resp.cacheResponse()?.body()!!.string())!!)
        }

        val info = moshi.adapter(Info::class.java).fromJson(resp.body()!!.string())!!
        return Either.Success(info)
    }


    private fun createTransaction(
            blockIdDetails: BlockIdDetails,
            actions: List<ActionAbi>
    ): TransactionAbi {
        return TransactionAbi(
                Date(Calendar.getInstance(TimeZone.getTimeZone("GMT:0:00")).timeInMillis + 30_000),
                blockIdDetails.blockNum,
                blockIdDetails.blockPrefix,
                0,
                0,
                0,
                0,
                0,
                emptyList(),
                actions,
                emptyList(),
                emptyList(),
                emptyList()
        )
    }

    private fun signTransactionAndGetSignature(info: Info,
                                               transaction: TransactionAbi,
                                               privateKey: EosPrivateKey,
                                               logLevel: LogLevel?,
                                               logger: HttpLoggingInterceptor.Logger?): String {
        val st = SignedTransactionAbi(info.chain_id, transaction, emptyList())

        if (logLevel == LogLevel.BODY) logger?.log("signed transaction = ${moshi
                .adapter(SignedTransactionAbi::class.java).toJson(st)}")

        return PrivateKeySigning()
                .sign(AbiBinaryGenTransactionWriter(CompressionType.NONE)
                        .squishSignedTransactionAbi(st, false)
                        .toBytes(), privateKey)

    }
}

private fun LogLevel?.adapt() = when (this) {
    null -> HttpLoggingInterceptor.Level.NONE
    LogLevel.BASIC -> HttpLoggingInterceptor.Level.BASIC
    LogLevel.BODY -> HttpLoggingInterceptor.Level.BODY
    LogLevel.NONE -> HttpLoggingInterceptor.Level.NONE
}

data class SignedTransactionResult(val transaction: TransactionAbi,
                                   val signedTransactionSignatures: List<String>,
                                   val usedChainInfo: Info)