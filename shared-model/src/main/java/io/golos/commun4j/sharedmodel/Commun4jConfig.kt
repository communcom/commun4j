package io.golos.commun4j.sharedmodel

import okhttp3.logging.HttpLoggingInterceptor

/** configuration object of [Commun4j]
 * @property blockChainHttpApiUrl url of cyber chain rest api
 * @property servicesUrl url of microservices socket gateway
 * @property connectionTimeOutInSeconds time for connection of  http request
 * @property readTimeoutInSeconds time to wait for socket or http response
 * @property writeTimeoutInSeconds time to wait for  http request
 * @property logLevel amount of logs. set LogLevel.NONE to disable
 * @property httpLogger callback for http logs
 * @property socketLogger callback for http socket
 * @property socketOpenQueryParams query params for socket. Each field of [SocketOpenQueryParams]
 * corresponds to query param name
 * **/


data class Commun4jConfig @JvmOverloads constructor(val blockChainHttpApiUrl: String,
                                                    val servicesUrl: String,
                                                    val connectionTimeOutInSeconds: Int = 30,
                                                    val readTimeoutInSeconds: Int = 30,
                                                    val writeTimeoutInSeconds: Int = 30,
                                                    val logLevel: LogLevel = LogLevel.BODY,
                                                    val httpLogger: HttpLoggingInterceptor.Logger? = HttpLoggingInterceptor.Logger.DEFAULT,
                                                    val socketLogger: HttpLoggingInterceptor.Logger? = null,
                                                    val socketOpenQueryParams: SocketOpenQueryParams)

data class SocketOpenQueryParams @JvmOverloads constructor(val version: String,
                                                           val deviceType: String = "phone",
                                                           val platform: String = "android",
                                                           val clientType: String = "app")