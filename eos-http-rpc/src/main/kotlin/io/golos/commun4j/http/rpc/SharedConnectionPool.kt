package io.golos.commun4j.http.rpc

object SharedConnectionPool {
    val pool = okhttp3.ConnectionPool()
}