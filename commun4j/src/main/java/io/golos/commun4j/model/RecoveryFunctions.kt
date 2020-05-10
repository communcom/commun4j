package io.golos.commun4j.model

import io.golos.commun4j.ITransactionPusherBridge
import io.golos.commun4j.abi.implementation.IAction
import io.golos.commun4j.abi.implementation.c.point.OpenCPointAction
import io.golos.commun4j.abi.implementation.c.point.OpenCPointStruct
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.core.crypto.EosPrivateKey
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.sharedmodel.Either
import io.golos.commun4j.sharedmodel.GolosEosError
import io.golos.commun4j.utils.toCyberName
import java.util.concurrent.Callable

typealias RecoverFunction = (error: GolosEosError,
                             originalAction: IAction,
                             keys: List<EosPrivateKey>,
                             endpoint: ITransactionPusherBridge,
                             bandWidthRequest: BandWidthRequest?,
                             clientAuthRequest: ClientAuthRequest?) -> Either<*, GolosEosError>


fun <T> callWithRecover(callable: Callable<Either<TransactionCommitted<T>, GolosEosError>>,
                        recovers: List<RecoverFunction>,
                        originalAction: IAction,
                        keys: List<EosPrivateKey>,
                        transactionPusher: ITransactionPusherBridge,
                        bandWidthRequest: BandWidthRequest?,
                        clientAuthRequest: ClientAuthRequest?): Either<TransactionCommitted<T>, GolosEosError> {

    var result: Either<TransactionCommitted<T>, GolosEosError>
    var recoverResult: Either<*, GolosEosError> = Either.Failure<Any, GolosEosError>(GolosEosError(0))

    do {
        result = callable.call()

        if (result is Either.Failure) {
            recovers
                    .forEach { recover ->
                       val result =  recover(result.value,
                                originalAction,
                                keys,
                                transactionPusher,
                                bandWidthRequest,
                                clientAuthRequest)
                        if (result is Either.Success) {
                            recoverResult = result
                        }
                    }
        }

    } while (result is Either.Failure && recoverResult is Either.Success)

    return result
}

fun recoverFromBalanceDoesNotExistError(error: GolosEosError,
                                        originalAction: IAction,
                                        keys: List<EosPrivateKey>,
                                        endpoint: ITransactionPusherBridge,
                                        bandWidthRequest: BandWidthRequest?,
                                        clientAuthRequest: ClientAuthRequest?): Either<*, GolosEosError> {

    if (!error.hasBalanceDoesNotExistError()) return Either.Failure<Any, GolosEosError>(error)

    val argsStruct = getOpenArgsCPointStructIfActionSupportedForBalanceNotExistError(originalAction)
            ?: return Either.Failure<Any, GolosEosError>(error)

    print(argsStruct)

    var actions = listOf(OpenCPointAction(argsStruct).toActionAbi(listOf(TransactionAuthorizationAbi(argsStruct.getOwner.name, "active"))))

    actions += createProvideBw(argsStruct.getOwner, "c".toCyberName())


    return endpoint.pushTransaction(
            actions,
            keys,
            OpenCPointStruct::class.java,
            bandWidthRequest
    )
}

fun recoverFromStaleError(error: GolosEosError,
                          originalAction: IAction,
                          keys: List<EosPrivateKey>,
                          endpoint: ITransactionPusherBridge,
                          bandWidthRequest: BandWidthRequest?,
                          clientAuthRequest: ClientAuthRequest?): Either<*, GolosEosError> {

    return if (error.error?.code == 3080006) Either.Success(Any())
    else Either.Failure<Any, GolosEosError>(error)
}