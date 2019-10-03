package io.golos.commun4j.chain.actions.query

import com.memtrip.eos.chain.actions.Config
import com.memtrip.eos.chain.actions.SetupTransactions
import com.memtrip.eos.chain.actions.generateUniqueAccountName
import com.memtrip.eos.chain.actions.transaction.account.DelegateBandwidthChain
import com.memtrip.eos.chain.actions.transactionDefaultExpiry
import com.memtrip.eos.core.crypto.EosPrivateKey

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(JUnitPlatform::class)
class GetDelegatedBandwidthTest : Spek({

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

        val setupTransactions by memoized { SetupTransactions(chainApi) }

        on("v1/chain/get_table_rows -> bandwidth") {

            val firstAccountName = generateUniqueAccountName()
            val firstAccountPrivateKey = EosPrivateKey()
            val firstAccountCreate = setupTransactions.createAccount(firstAccountName, firstAccountPrivateKey).blockingGet()
            val firstAccountTransfer = setupTransactions.transfer(firstAccountName).blockingGet()

            val secondAccountName = generateUniqueAccountName()
            val secondAccountPrivateKey = EosPrivateKey()
            val secondAccountCreate = setupTransactions.createAccount(secondAccountName, secondAccountPrivateKey).blockingGet()
            val secondAccountTransfer = setupTransactions.transfer(secondAccountName).blockingGet()

            /* delegate first to self */
            val firstDelegateBw = DelegateBandwidthChain(chainApi).delegateBandwidth(
                DelegateBandwidthChain.Args(
                    firstAccountName,
                    firstAccountName,
                    "0.0001 EOS",
                    "0.0001 EOS",
                    false
                ),
                    io.golos.commun4j.chain.actions.transaction.TransactionContext(
                            firstAccountName,
                            firstAccountPrivateKey,
                            transactionDefaultExpiry()
                    )
            ).blockingGet()

            /* delegate first to second */
            val secondDelegateBw = DelegateBandwidthChain(chainApi).delegateBandwidth(
                DelegateBandwidthChain.Args(
                    firstAccountName,
                    secondAccountName,
                    "0.0001 EOS",
                    "0.0001 EOS",
                    false
                ),
                    io.golos.commun4j.chain.actions.transaction.TransactionContext(
                            firstAccountName,
                            firstAccountPrivateKey,
                            transactionDefaultExpiry()
                    )
            ).blockingGet()

            val response = io.golos.commun4j.chain.actions.query.bandwidth.GetDelegatedBandwidth(chainApi).getBandwidth(firstAccountName).blockingGet()

            it("should return the delegated bandwidth") {
                assertTrue(firstAccountCreate.isSuccessful)
                assertTrue(firstAccountTransfer.isSuccessful)
                assertTrue(secondAccountCreate.isSuccessful)
                assertTrue(secondAccountTransfer.isSuccessful)
                assertTrue(firstDelegateBw.isSuccessful)
                assertTrue(secondDelegateBw.isSuccessful)

                assertEquals(2, response.size)

                // The order of delegated bandwidth items is inconsistent, this work around is used to increase
                // the stability of the integration tests.
                // TODO: Why is the order of delegate bandwidth inconsistent?
                if (response[0].from == response[0].to) {
                    assertEquals(response[0].from, firstAccountName)
                    assertEquals(response[0].to, firstAccountName)
                    assertEquals(response[0].net_weight, "0.1001 EOS")
                    assertEquals(response[0].cpu_weight, "1.0001 EOS")
                    assertEquals(response[1].from, firstAccountName)
                    assertEquals(response[1].to, secondAccountName)
                    assertEquals(response[1].net_weight, "0.0001 EOS")
                    assertEquals(response[1].cpu_weight, "0.0001 EOS")
                } else {
                    assertEquals(response[0].from, firstAccountName)
                    assertEquals(response[0].to, secondAccountName)
                    assertEquals(response[0].net_weight, "0.0001 EOS")
                    assertEquals(response[0].cpu_weight, "0.0001 EOS")
                    assertEquals(response[1].from, firstAccountName)
                    assertEquals(response[1].to, firstAccountName)
                    assertEquals(response[1].net_weight, "0.1001 EOS")
                    assertEquals(response[1].cpu_weight, "1.0001 EOS")
                }
            }
        }
    }
})