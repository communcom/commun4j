package io.golos.commun4j.chain.actions.query

import com.memtrip.eos.chain.actions.Config
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(JUnitPlatform::class)
class GetBlockProducersTest : Spek({

    given("an Api") {

        val okHttpClient by memoized {
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build()
        }

        val chainApi by memoized { io.golos.commun4j.http.rpc.Api(Config.MAINNET_API_BASE_URL, okHttpClient).chain }

        on("v1/chain/get_producers") {

            val response = io.golos.commun4j.chain.actions.query.producer.GetBlockProducers(chainApi).getProducers(50).blockingGet()

            it("should return the transaction") {
                response.map { blockProducer ->
                    assertFalse(blockProducer.apiEndpoint.isEmpty())
                }
            }
        }

        on("v1/chain/get_producers -> single") {

            val response = io.golos.commun4j.chain.actions.query.producer.GetBlockProducers(chainApi).getSingleProducer("eoscannonchn").blockingGet()

            it("should return the transaction") {
                assertFalse(response.apiEndpoint.isEmpty())
                assertEquals(response.bpJson.producer_account_name, "eoscannonchn")
            }
        }
    }
})