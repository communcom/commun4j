package io.golos.commun4j.chain.actions.query.ramprice

import io.golos.commun4j.http.rpc.model.contract.request.GetTableRows
import io.reactivex.Single
import java.math.BigDecimal

class GetRamPrice(
        private val chainApi: io.golos.commun4j.http.rpc.ChainApi
) {

    fun getPricePerKilobyte(): Single<Double> {
        return chainApi.getTableRows(GetTableRows(
                "eosio",
                "eosio",
                "rammarket",
                "",
                true,
                1,
                "",
                "",
                "",
                "",
                "dec"
        )).map { response ->
            if (response.isSuccessful) {
                val tableRows = response.body()!!.rows
                if (tableRows.isNotEmpty()) {
                    calculateRamPerKiloByte(tableRows[0])
                } else {
                    throw io.golos.commun4j.chain.actions.query.ramprice.GetRamPrice.FailedToGetRamPrice()
                }
            } else {
                throw io.golos.commun4j.chain.actions.query.ramprice.GetRamPrice.FailedToGetRamPrice()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun calculateRamPerKiloByte(row: Map<String, Any>): Double {
        val quote = row["quote"] as Map<String, String>
        val base = row["base"] as Map<String, String>

        val quoteBalance = value(quote["balance"]!!)
        val baseBalance = value(base["balance"]!!)

        return (quoteBalance.toDouble() / (baseBalance.toDouble()) * 1024)
    }

    private fun value(balance: String): BigDecimal {
        val split = balance.split(" ")[0]
        return BigDecimal(split)
    }

    class FailedToGetRamPrice : Exception()
}