package io.golos.commun4j.http.rpc.chain

import com.memtrip.eos.http.rpc.model.account.request.AccountName
import com.memtrip.eos.http.rpc.utils.Config
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(JUnitPlatform::class)
class ChainGetAccountTest : Spek({

    given("an Api") {

        val okHttpClient by memoized {
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build()
        }

        val chainApi by memoized { io.golos.commun4j.http.rpc.Api(Config.CHAIN_API_BASE_URL, okHttpClient).chain }

        on("v1/chain/get_account -> eosio.token") {

            val account = chainApi.getAccount(AccountName("eosio.token")).blockingGet()

            it("should return the account") {
                assertTrue(account.isSuccessful)
                assertNotNull(account.body())
            }
        }

        on("v1/chain/get_account -> voter info") {

            val account = chainApi.getAccount(AccountName("memtripadmin")).blockingGet()

            it("should return the account") {
                assertTrue(account.isSuccessful)
                assertNotNull(account.body()!!.voter_info)
                assertTrue(account.body()!!.voter_info!!.producers.isNotEmpty())
            }
        }

        on("v1/chain/get_account -> voter info proxied") {

            val account = chainApi.getAccount(AccountName("memtripproxy")).blockingGet()

            it("should return the account") {
                assertTrue(account.isSuccessful)
                assertNotNull(account.body())
                assertTrue(account.body()!!.voter_info!!.is_proxy == 1)
            }
        }
    }
})