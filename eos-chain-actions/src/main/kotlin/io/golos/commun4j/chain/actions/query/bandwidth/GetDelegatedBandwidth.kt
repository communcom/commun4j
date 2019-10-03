package io.golos.commun4j.chain.actions.query.bandwidth

import io.golos.commun4j.http.rpc.model.contract.request.GetTableRows
import io.reactivex.Single

class GetDelegatedBandwidth(private val chainApi: io.golos.commun4j.http.rpc.ChainApi) {

    fun getBandwidth(accountName: String): Single<List<io.golos.commun4j.chain.actions.query.bandwidth.BandwidthJson>> {
        return chainApi.getTableRows(GetTableRows(
                accountName,
                "eosio",
                "delband",
                "",
                true,
                100,
                "",
                "",
                "",
                "",
                "dec"
        )).map { response ->
            if (response.isSuccessful && response.body() != null) {
                val rows = response.body()!!.rows
                rows.map { row ->
                    io.golos.commun4j.chain.actions.query.bandwidth.BandwidthJson(
                            row["from"].toString(),
                            row["to"].toString(),
                            row["net_weight"].toString(),
                            row["cpu_weight"].toString())
                }
            } else {
                throw io.golos.commun4j.chain.actions.query.bandwidth.GetDelegatedBandwidth.FailedToFetchDelegatedBandwidth()
            }
        }
    }

    class FailedToFetchDelegatedBandwidth : Exception()
}