package io.golos.commun4j

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.golos.commun4j.http.rpc.model.account.request.AccountName
import io.golos.commun4j.http.rpc.model.account.response.Account
import io.golos.commun4j.http.rpc.model.block.request.BlockNumOrId
import io.golos.commun4j.http.rpc.model.block.response.Block
import io.golos.commun4j.http.rpc.model.block.response.BlockHeaderState
import io.golos.commun4j.http.rpc.model.contract.request.*
import io.golos.commun4j.http.rpc.model.contract.response.*
import io.golos.commun4j.http.rpc.model.info.Info
import io.golos.commun4j.http.rpc.model.producer.request.GetProducers
import io.golos.commun4j.http.rpc.model.producer.response.ProducerList
import io.golos.commun4j.http.rpc.model.signing.GetRequiredKeysBody
import io.golos.commun4j.http.rpc.model.signing.PushTransaction
import io.golos.commun4j.http.rpc.model.signing.RequiredKeys
import io.golos.commun4j.model.CyberWayChainApi
import io.golos.commun4j.model.ResolvedName
import io.golos.commun4j.services.model.ImageUploadResponse
import io.golos.commun4j.sharedmodel.Commun4jConfig
import io.golos.commun4j.sharedmodel.LogLevel
import io.reactivex.Single
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

interface ChainApiProvider {
    fun provide(): CyberWayChainApi
}

internal class GolosEosConfiguratedApi(private val config: Commun4jConfig,
                                       private val moshi: Moshi) : ChainApiProvider {

    private val api: CyberWayChainApi
    private val okHttpClient: OkHttpClient


    init {
        val interceptor = if (config.httpLogger != null) HttpLoggingInterceptor(config.httpLogger!!)
        else HttpLoggingInterceptor()
        interceptor.level =
                when (config.logLevel) {
                    LogLevel.BASIC -> HttpLoggingInterceptor.Level.BASIC
                    LogLevel.BODY -> HttpLoggingInterceptor.Level.BODY
                    LogLevel.NONE -> HttpLoggingInterceptor.Level.NONE
                }

        okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(config.connectionTimeOutInSeconds.toLong(), TimeUnit.SECONDS)
                .readTimeout(config.readTimeoutInSeconds.toLong(), TimeUnit.SECONDS)
                .writeTimeout(config.writeTimeoutInSeconds.toLong(), TimeUnit.SECONDS)
                .dispatcher(Dispatcher(Executors.newSingleThreadExecutor { Thread.currentThread() }))
                .connectionPool(io.golos.commun4j.http.rpc.SharedConnectionPool.pool)
                .build()

        api = object : CyberWayChainApi {
            val api = io.golos.commun4j.http.rpc.Api(config.blockChainHttpApiUrl, okHttpClient).chain
            override fun getInfo(): Single<Response<Info>> {
                return api.getInfo()
            }

            override fun getProducers(body: GetProducers): Single<Response<ProducerList>> {
                return api.getProducers(body)
            }

            override fun getBlock(body: BlockNumOrId): Single<Response<Block>> {
                return api.getBlock(body)
            }

            override fun getBlockHeaderState(body: BlockNumOrId): Single<Response<BlockHeaderState>> {
                return api.getBlockHeaderState(body)
            }

            override fun getAccount(body: AccountName): Single<Response<Account>> {
                return api.getAccount(body)
            }

            override fun getAbi(body: AccountName): Single<Response<AbiForAccount>> {
                return api.getAbi(body)
            }

            override fun getCode(body: GetCodeByAccountName): Single<Response<CodeForAccount>> {
                return api.getCode(body)
            }

            override fun getRawCodeAndAbi(body: AccountName): Single<Response<RawCodeForAccount>> {
                return api.getRawCodeAndAbi(body)
            }

            override fun getTableRows(body: GetTableRows): Single<Response<ContractTableRows>> {
                return api.getTableRows(body)
            }

            override fun getCurrencyBalance(body: GetCurrencyBalance): Single<Response<List<String>>> {
                return api.getCurrencyBalance(body)
            }

            override fun abiJsonToBin(body: RequestBody): Single<Response<BinaryHex>> {
                return api.abiJsonToBin(body)
            }

            override fun abiBinToJson(body: AbiBinToJson): Single<Response<ResponseBody>> {
                return api.abiBinToJson(body)
            }

            override fun getRequiredKeys(body: GetRequiredKeysBody): Single<Response<RequiredKeys>> {
                return api.getRequiredKeys(body)
            }

            override fun getCurrencyStats(body: GetCurrencyStats): Single<Response<ResponseBody>> {
                return api.getCurrencyStats(body)
            }

            override fun pushTransaction(body: PushTransaction): Single<Response<String>> {
                return Single.create<Response<String>> { emitter ->

                    try {
                        val resp = okHttpClient.newCall(Request.Builder()
                                .url("${config.blockChainHttpApiUrl}v1/chain/push_transaction")
                                .post(
                                        moshi
                                                .adapter<PushTransaction>(PushTransaction::class.java)
                                                .toJson(body)
                                                .toRequestBody("application/json"
                                                        .toMediaType())).build())
                                .execute()

                        val responseBody = resp.body

                        if (responseBody == null) emitter.onError(IllegalStateException("null response $resp"))
                        val responseString = responseBody!!.string()
                        try {
                            emitter.onSuccess(Response.success(responseString))
                        } catch (e: Exception) {
                            emitter.onError(e)
                        }

                    } catch (e: java.lang.Exception) {
                        emitter.onError(e)
                    }
                }
            }

            override fun shutDown() {
                okHttpClient.connectionPool.evictAll()
                okHttpClient.dispatcher.executorService.shutdown()
            }

            override fun resolveNames(body: List<String>): Single<List<ResolvedName>> {

                return Single.create<List<ResolvedName>> { emitter ->
                    val type = Types.newParameterizedType(List::class.java, String::class.java)

                    try {
                        val resp = okHttpClient.newCall(Request.Builder()
                                .url("${config.blockChainHttpApiUrl}v1/chain/resolve_names")
                                .post(
                                        moshi.adapter<List<String>>(type)
                                                .toJson(body)
                                                .toRequestBody(
                                                        "application/json"
                                                                .toMediaType()))
                                .build())
                                .execute()

                        val responseBody = resp.body
                        if (responseBody == null) emitter.onError(IllegalStateException("null response $resp"))
                        val responseString = responseBody!!.string()
                        try {
                            val resolvedName = moshi.adapter<List<ResolvedName>>(Types.newParameterizedType(List::class.java,
                                    ResolvedName::class.java)).fromJson(responseString)
                            emitter.onSuccess(resolvedName ?: listOf())
                        } catch (e: Exception) {
                            emitter.onError(e)
                        }

                    } catch (e: java.lang.Exception) {
                        emitter.onError(e)
                    }
                }
            }

            override fun uploadImage(file: File): Single<String> {
                return Single.create { emitter ->
                    try {
                        val resp = okHttpClient.newCall(Request.Builder()
                                .url("https://img.golos.io/upload")
                                .post(MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("file", file.name,
                                                file.asRequestBody("image/png".toMediaType()))
                                        .build())
                                .build())
                                .execute()
                        try {
                            val message = resp.body!!.string()
                            if (resp.isSuccessful) {
                                val uri = moshi.adapter<ImageUploadResponse>(ImageUploadResponse::class.java).fromJson(message)
                                emitter.onSuccess(uri!!.url)
                            } else {
                                emitter.onError(IOException(message))
                            }

                        } catch (e: java.lang.Exception) {
                            emitter.onError(e)
                        }

                    } catch (e: java.lang.Exception) {
                        emitter.onError(e)
                    }
                }
            }
        }
    }


    override fun provide(): CyberWayChainApi {
        return api
    }
}