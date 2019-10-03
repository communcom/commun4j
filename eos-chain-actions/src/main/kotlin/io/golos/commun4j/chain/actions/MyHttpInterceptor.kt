package io.golos.commun4j.chain.actions

/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import okhttp3.*
import okhttp3.internal.http.HttpHeaders
import okio.Buffer
import okio.GzipSource
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import okhttp3.logging.HttpLoggingInterceptor.Logger
import okhttp3.logging.HttpLoggingInterceptor.Level
import java.lang.ref.WeakReference


/**
 * An OkHttp interceptor which logs request and response information. Can be applied as an
 * [application interceptor][OkHttpClient.interceptors] or as a [ ][OkHttpClient.networkInterceptors].
 *
 * The format of the logs created by
 * this class should not be considered stable and may change slightly between releases. If you need
 * a stable logging format, use your own interceptor.
 */
class MyHttpInterceptor constructor() : Interceptor {


    val loggers = ConcurrentHashMap<Request, Pair<WeakReference<Logger>, Level>>()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {


        val request = chain.request()

        val level = loggers[request]?.second?: Level.NONE
        val logger = loggers[request]?.first?.get()
        if (level == Level.NONE || logger == null) {
            return chain.proceed(request)
        }

        val logBody = level == Level.BODY
        val logHeaders = logBody || level == Level.HEADERS

        val requestBody = request.body()
        val hasRequestBody = requestBody != null

        val connection = chain.connection()
        var requestStartMessage = ("--> "
                + request.method()
                + ' '.toString() + request.url()
                + if (connection != null) " " + connection.protocol() else "")
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody!!.contentLength() + "-byte body)"
        }
        logger.log(requestStartMessage)

        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody!!.contentType() != null) {
                    logger.log("Content-Type: " + requestBody.contentType()!!)
                }
                if (requestBody.contentLength() != -1L) {
                    logger.log("Content-Length: " + requestBody.contentLength())
                }
            }

            val headers = request.headers()
            var i = 0
            val count = headers.size()
            while (i < count) {
                val name = headers.name(i)
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equals(name, ignoreCase = true) && !"Content-Length".equals(name, ignoreCase = true)) {
                    logger.log(name + ": " + headers.value(i))
                }
                i++
            }

            if (!logBody || !hasRequestBody) {
                logger.log("--> END " + request.method())
            } else if (bodyHasUnknownEncoding(request.headers())) {
                logger.log("--> END " + request.method() + " (encoded body omitted)")
            } else {
                val buffer = Buffer()
                requestBody!!.writeTo(buffer)

                var charset: Charset? = io.golos.commun4j.chain.actions.MyHttpInterceptor.Companion.UTF8
                val contentType = requestBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(io.golos.commun4j.chain.actions.MyHttpInterceptor.Companion.UTF8)
                }

                logger.log("")
                if (io.golos.commun4j.chain.actions.MyHttpInterceptor.Companion.isPlaintext(buffer)) {
                    logger.log(buffer.readString(charset!!))
                    logger.log("--> END " + request.method()
                            + " (" + requestBody.contentLength() + "-byte body)")
                } else {
                    logger.log("--> END " + request.method() + " (binary "
                            + requestBody.contentLength() + "-byte body omitted)")
                }
            }
        }

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            logger.log("<-- HTTP FAILED: $e")
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body()
        val contentLength = responseBody!!.contentLength()
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
        logger.log("<-- "
                + response.code()
                + (if (response.message().isEmpty()) "" else ' ' + response.message())
                + ' '.toString() + response.request().url()
                + " (" + tookMs + "ms" + (if (!logHeaders) ", $bodySize body" else "") + ')'.toString())

        if (logHeaders) {
            val headers = response.headers()
            var i = 0
            val count = headers.size()
            while (i < count) {
                logger.log(headers.name(i) + ": " + headers.value(i))
                i++
            }

            if (!logBody || !HttpHeaders.hasBody(response)) {
                logger.log("<-- END HTTP")
            } else if (bodyHasUnknownEncoding(response.headers())) {
                logger.log("<-- END HTTP (encoded body omitted)")
            } else {
                val source = responseBody.source()
                source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
                var buffer = source.buffer()

                var gzippedLength: Long? = null
                if ("gzip".equals(headers.get("Content-Encoding")?:"", ignoreCase = true)) {
                    gzippedLength = buffer.size()
                    var gzippedResponseBody: GzipSource? = null
                    try {
                        gzippedResponseBody = GzipSource(buffer.clone())
                        buffer = Buffer()
                        buffer.writeAll(gzippedResponseBody)
                    } finally {
                        gzippedResponseBody?.close()
                    }
                }

                var charset: Charset? = io.golos.commun4j.chain.actions.MyHttpInterceptor.Companion.UTF8
                val contentType = responseBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(io.golos.commun4j.chain.actions.MyHttpInterceptor.Companion.UTF8)
                }

                if (!io.golos.commun4j.chain.actions.MyHttpInterceptor.Companion.isPlaintext(buffer)) {
                    logger.log("")
                    logger.log("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)")
                    return response
                }

                if (contentLength != 0L) {
                    logger.log("")
                    logger.log(buffer.clone().readString(charset!!))
                }

                if (gzippedLength != null) {
                    logger.log("<-- END HTTP (" + buffer.size() + "-byte, "
                            + gzippedLength + "-gzipped-byte body)")
                } else {
                    logger.log("<-- END HTTP (" + buffer.size() + "-byte body)")
                }
            }
        }

        return response
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return (contentEncoding != null
                && !contentEncoding.equals("identity", ignoreCase = true)
                && !contentEncoding.equals("gzip", ignoreCase = true))
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")

        /**
         * Returns true if the body in question probably contains human readable text. Uses a small sample
         * of code points to detect unicode control characters commonly used in binary file signatures.
         */
        internal fun isPlaintext(buffer: Buffer): Boolean {
            try {
                val prefix = Buffer()
                val byteCount = if (buffer.size() < 64) buffer.size() else 64
                buffer.copyTo(prefix, 0, byteCount)
                for (i in 0..15) {
                    if (prefix.exhausted()) {
                        break
                    }
                    val codePoint = prefix.readUtf8CodePoint()
                    if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                        return false
                    }
                }
                return true
            } catch (e: EOFException) {
                return false // Truncated UTF-8 sequence.
            }

        }
    }
}
