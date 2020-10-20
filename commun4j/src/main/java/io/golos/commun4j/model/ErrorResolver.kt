package io.golos.commun4j.model

import io.golos.commun4j.sharedmodel.GolosEosError

fun GolosEosError.hasBalanceOfFromNotOpened(): Boolean = error
        ?.details
        ?.any {
            it
                    .message
                    ?.contains("assertion failure with message: balance of from not opened", true) == true
        } == true

fun GolosEosError.hasBalanceDoesNotExistError(): Boolean = error
        ?.details
        ?.any {
            it
                    .message
                    ?.contains("balance does not exist", true) == true
        } == true
