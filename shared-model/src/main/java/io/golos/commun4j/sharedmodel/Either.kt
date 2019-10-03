package io.golos.commun4j.sharedmodel

/** wrapper class encapsulating success of failure of certain action.
 * Being closed class hierarchy, it can be only [io.golos.commun4j.utils.Either.Success] or [io.golos.commun4j.utils.Either.Failure]
 * Proper use be like
 * fun someFunc(): Either<SuccessResponse, FailResponse>{/**/}
 *
 * val result = someFunc()
 *
 * if (result is Success){
 *  //happy path
 *     val success: SuccessResponse = result.value
 *  } else {
 *  //failure
 *      val failure: FailResponse = result.value
 *   }
 *
 * */
sealed class Either<S, F> {
    data class Success<S, F>(val value: S) : Either<S, F>()
    data class Failure<S, F>(val value: F) : Either<S, F>()
}

