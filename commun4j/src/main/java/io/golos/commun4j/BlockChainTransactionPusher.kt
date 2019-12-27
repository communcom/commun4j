package io.golos.commun4j

import io.golos.commun4j.chain.actions.ITransactionPusher
import io.golos.commun4j.chain.actions.SignedTransactionProvider
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.core.crypto.EosPrivateKey
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.model.BandWidthRequest
import io.golos.commun4j.model.BandWidthSource
import io.golos.commun4j.services.model.ServicesTransactionPushService
import io.golos.commun4j.sharedmodel.Commun4jConfig
import io.golos.commun4j.sharedmodel.Either
import io.golos.commun4j.sharedmodel.GolosEosError
import io.golos.commun4j.sharedmodel.LogLevel
import okhttp3.logging.HttpLoggingInterceptor


interface ITransactionPusherBridge {
    fun <T : Any> pushTransaction(action: List<ActionAbi>,
                                  keys: List<EosPrivateKey>,
                                  traceType: Class<T>,
                                  bandWidthSource: BandWidthRequest?): Either<TransactionCommitted<T>, GolosEosError>
}

internal class TransactionPusherBridge(private val commun4JConfig: Commun4jConfig,
                                       private val blockChainPusher: ITransactionPusher,
                                       private val servicesPusher: ITransactionPusher) : ITransactionPusherBridge {

    override fun <T : Any> pushTransaction(action: List<ActionAbi>,
                                           keys: List<EosPrivateKey>,
                                           traceType: Class<T>,
                                           bandWidthSource: BandWidthRequest?): Either<TransactionCommitted<T>, GolosEosError> {

        return if (bandWidthSource?.source == BandWidthSource.COMN_SERVICES) {

            servicesPusher.push(action, keys, traceType, commun4JConfig.blockChainHttpApiUrl, commun4JConfig.logLevel, commun4JConfig.httpLogger)

        } else blockChainPusher.push(action, keys, traceType, commun4JConfig.blockChainHttpApiUrl, commun4JConfig.logLevel, commun4JConfig.httpLogger)
    }
}


class ServicesTransactionPusher(private val commun4JConfig: Commun4jConfig,
                                private val service: ServicesTransactionPushService,
                                private val signedTransactionProvider: SignedTransactionProvider) : ITransactionPusher {

    override fun <T : Any> push(actions: List<ActionAbi>, keys: List<EosPrivateKey>, traceType: Class<T>, blockChainUrl: String, logLevel: LogLevel?, logger: HttpLoggingInterceptor.Logger?): Either<TransactionCommitted<T>, GolosEosError> {

        val signedTrx =
                signedTransactionProvider
                        .createSignedTransaction(actions,
                                keys.toList(), commun4JConfig.blockChainHttpApiUrl,
                                commun4JConfig.logLevel, commun4JConfig.httpLogger)

        if (signedTrx is Either.Failure) return Either.Failure(signedTrx.value)

        signedTrx as Either.Success
        return service.pushTransactionWithProvidedBandwidth(signedTrx.value.usedChainInfo.chain_id,
                signedTrx.value.transaction,
                signedTrx.value.signedTransactionSignatures.first(),
                traceType)
    }
}