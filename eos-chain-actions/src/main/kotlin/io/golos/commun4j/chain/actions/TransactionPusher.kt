package io.golos.commun4j.chain.actions

import io.golos.commun4j.chain.actions.transaction.SignedTransactionResult
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.core.crypto.EosPrivateKey
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.sharedmodel.Either
import io.golos.commun4j.sharedmodel.GolosEosError
import io.golos.commun4j.sharedmodel.LogLevel
import okhttp3.logging.HttpLoggingInterceptor

interface ITransactionPusher {
    fun <T : Any> push(actions: List<ActionAbi>,
                       keys: List<EosPrivateKey>,
                       traceType: Class<T>,
                       blockChainUrl: String,
                       logLevel: LogLevel? = null,
                       logger: HttpLoggingInterceptor.Logger? = null): Either<TransactionCommitted<T>, GolosEosError>
}

interface SignedTransactionProvider {
    fun createSignedTransaction(
            action: List<ActionAbi>,
            keys: List<EosPrivateKey>,
            blockChainUrl: String,
            logLevel: LogLevel? = null,
            logger: HttpLoggingInterceptor.Logger? = null): Either<SignedTransactionResult, GolosEosError>
}