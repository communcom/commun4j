package io.golos.commun4j.sharedmodel

import okhttp3.logging.HttpLoggingInterceptor

data class Commun4jConfig @JvmOverloads constructor(val blockChainHttpApiUrl: String,// url of cyber chain rest api
                                                    val servicesUrl: String, //url of microservices gateway
                                                    val connectionTimeOutInSeconds: Int = 30,// time to wait unused socket or unresponsive http request to wait before drop
                                                    val readTimeoutInSeconds: Int = 30,// time to wait read from socket or  http request to wait before drop
                                                    val writeTimeoutInSeconds: Int = 30,// time to wait write from socket or  http request to wait before drop
                                                    val logLevel: LogLevel = LogLevel.BODY,// amount of logs. set LogLevel.NONE to disable
                                                    val httpLogger: HttpLoggingInterceptor.Logger? = HttpLoggingInterceptor.Logger.DEFAULT,
                                                    val socketLogger: HttpLoggingInterceptor.Logger? = null)
