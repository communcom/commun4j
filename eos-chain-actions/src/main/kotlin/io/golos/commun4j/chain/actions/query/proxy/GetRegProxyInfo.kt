package io.golos.commun4j.chain.actions.query.proxy

import io.golos.commun4j.abi.writer.bytewriter.NameWriter
import io.golos.commun4j.http.rpc.model.contract.request.GetTableRows
import io.reactivex.Single

class GetRegProxyInfo(
        private val chainApi: io.golos.commun4j.http.rpc.ChainApi
) {

    fun getProxies(limit: Int, nextAccount: String = ""): Single<List<io.golos.commun4j.chain.actions.query.proxy.ProxyJson>> {
        return chainApi.getTableRows(GetTableRows(
                "regproxyinfo",
                "regproxyinfo",
                "proxies",
                "",
                true,
                limit,
                if (nextAccount.isEmpty()) {
                    ""
                } else {
                    (NameWriter().eosNameAsLong(nextAccount) + 1).toString()
                },
                "",
                "",
                "",
                "dec"
        )).map { response ->
            if (response.isSuccessful) {
                response.body()!!.rows.map { row ->
                    io.golos.commun4j.chain.actions.query.proxy.ProxyJson(
                            row["owner"].toString(),
                            row["name"].toString(),
                            row["website"].toString(),
                            row["slogan"].toString(),
                            row["philosophy"].toString(),
                            row["background"].toString(),
                            row["logo_256"].toString(),
                            row["telegram"].toString(),
                            row["steemit"].toString(),
                            row["twitter"].toString(),
                            row["wechat"].toString())
                }
            } else {
                throw io.golos.commun4j.chain.actions.query.proxy.GetRegProxyInfo.FailedToFetchProxies()
            }
        }
    }

    fun getProxy(accountName: String): Single<io.golos.commun4j.chain.actions.query.proxy.ProxyJson> {
        return chainApi.getTableRows(GetTableRows(
                "regproxyinfo",
                "regproxyinfo",
                "proxies",
                "",
                true,
                1,
                NameWriter().eosNameAsLong(accountName).toString(),
                (NameWriter().eosNameAsLong(accountName) + 1).toString(),
                "",
                "",
                "dec"
        )).map { response ->
            if (response.isSuccessful && response.body()!!.rows.isNotEmpty()) {
                val rows = response.body()!!.rows
                io.golos.commun4j.chain.actions.query.proxy.ProxyJson(
                        rows[0]["owner"].toString(),
                        rows[0]["name"].toString(),
                        rows[0]["website"].toString(),
                        rows[0]["slogan"].toString(),
                        rows[0]["philosophy"].toString(),
                        rows[0]["background"].toString(),
                        rows[0]["logo_256"].toString(),
                        rows[0]["telegram"].toString(),
                        rows[0]["steemit"].toString(),
                        rows[0]["twitter"].toString(),
                        rows[0]["wechat"].toString())
            } else {
                throw io.golos.commun4j.chain.actions.query.proxy.GetRegProxyInfo.FailedToFetchProxies()
            }
        }
    }

    class FailedToFetchProxies : Exception()
}