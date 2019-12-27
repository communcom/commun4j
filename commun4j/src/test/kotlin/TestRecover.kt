import io.golos.commun4j.ITransactionPusherBridge
import io.golos.commun4j.abi.implementation.IAction
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.core.crypto.EosPrivateKey
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionParentReceipt
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionProcessed
import io.golos.commun4j.model.BandWidthRequest
import io.golos.commun4j.model.ClientAuthRequest
import io.golos.commun4j.model.callWithRecover
import io.golos.commun4j.sharedmodel.Either
import io.golos.commun4j.sharedmodel.GolosEosError
import org.junit.Assert
import org.junit.Test
import org.omg.CORBA.Any
import java.util.concurrent.Callable

class TestRecover {

    val stubTransactionCommitted =
            TransactionCommitted<Any>("",
                    TransactionProcessed(
                            "", TransactionParentReceipt("", 0, 0),
                            0, 0, false, emptyList(), null, 0
                    ),
                    null)

    fun alwaysSuccessRecover(error: GolosEosError,
                             originalAction: IAction,
                             keys: List<EosPrivateKey>,
                             endpoint: ITransactionPusherBridge,
                             bandWidthRequest: BandWidthRequest?,
                             clientAuthRequest: ClientAuthRequest?): Either<*, GolosEosError> {
        return Either.Success(Object())
    }

    fun alwaysFailRecover(error: GolosEosError,
                          originalAction: IAction,
                          keys: List<EosPrivateKey>,
                          endpoint: ITransactionPusherBridge,
                          bandWidthRequest: BandWidthRequest?,
                          clientAuthRequest: ClientAuthRequest?): Either<*, GolosEosError> {

        return Either.Failure<Any, GolosEosError>(GolosEosError(0))
    }

    val stubIAction = object : IAction {
        override fun asActionAbi(transactionAuth: List<TransactionAuthorizationAbi>): ActionAbi {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
    val stubPusherBridge: ITransactionPusherBridge = object : ITransactionPusherBridge {
        override fun <T : kotlin.Any> pushTransaction(action: List<ActionAbi>, keys: List<EosPrivateKey>, traceType: Class<T>, bandWidthSource: BandWidthRequest?): Either<TransactionCommitted<T>, GolosEosError> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    @Test
    fun testSuccess() {
        val callable = Callable<Either<TransactionCommitted<Any>, GolosEosError>> {
            Either.Success(stubTransactionCommitted)
        }


        val result = callWithRecover(callable, listOf(::alwaysSuccessRecover),
                stubIAction, emptyList(), stubPusherBridge, null, null)

        Assert.assertTrue("transaction success must return it's success", result is Either.Success)
    }

    @Test
    fun testFailure() {
        val failCallable = Callable<Either<TransactionCommitted<Any>, GolosEosError>> {
            Either.Failure(GolosEosError(0))
        }


        val result = callWithRecover(failCallable, listOf(::alwaysFailRecover),
                stubIAction, emptyList(), stubPusherBridge, null, null)

        Assert.assertTrue("transaction fail and recover fail must return fail", result is Either.Failure)
    }

    @Test
    fun testRecoverFromFailure() {
        val failCallable = object : Callable<Either<TransactionCommitted<Any>, GolosEosError>> {
            var counter = -1

            override fun call(): Either<TransactionCommitted<Any>, GolosEosError> {
                counter++
                return if (counter == 0) Either.Failure(GolosEosError(0))
                else Either.Success(stubTransactionCommitted)
            }
        }


        val result = callWithRecover(failCallable, listOf(::alwaysSuccessRecover,
                ::alwaysFailRecover),
                stubIAction, emptyList(), stubPusherBridge, null, null)

        Assert.assertTrue("transaction fail and recover success, overall must be success", result is Either.Success)
        Assert.assertEquals("result must be from callable second call", result.getOrThrow(), stubTransactionCommitted)
    }
}