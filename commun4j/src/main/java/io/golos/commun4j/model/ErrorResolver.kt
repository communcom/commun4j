package io.golos.commun4j.model

import io.golos.commun4j.sharedmodel.GolosEosError

fun GolosEosError.hasBalanceDoesNotExistError(): Boolean = error
        ?.details
        ?.any {
            it
                    .message
                    ?.contains("balance does not exist", true) == true
        } == true