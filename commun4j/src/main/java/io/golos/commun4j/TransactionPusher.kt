package io.golos.commun4j

import io.golos.commun4j.chain.actions.transaction.TransactionPusher
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.core.crypto.EosPrivateKey
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.model.BandWidthRequest
import io.golos.commun4j.sharedmodel.Commun4jConfig
import io.golos.commun4j.sharedmodel.Either
import io.golos.commun4j.sharedmodel.GolosEosError
import java.util.*

internal class TransactionPusherImpl(private val commun4JConfig: Commun4jConfig) {


    fun <T : Any> pushTransaction(action: List<ActionAbi>,
                                  key: EosPrivateKey,
                                  traceType: Class<T>,
                                  additionalKeys: List<EosPrivateKey>? = null,
                                  bandWidthSource: BandWidthRequest? = null): Either<TransactionCommitted<T>, GolosEosError> {

        return TransactionPusher.pushTransaction(action,
                (additionalKeys.orEmpty() + bandWidthSource?.key.toList() + key).toSet(),
                traceType, commun4JConfig.blockChainHttpApiUrl,
                commun4JConfig.logLevel,
                commun4JConfig.httpLogger)
    }
}

private fun <T> T?.toList(): List<T> = if (this == null) Collections.emptyList() else Collections.singletonList(this)
