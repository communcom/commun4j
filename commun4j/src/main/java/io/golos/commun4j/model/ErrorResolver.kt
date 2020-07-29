package io.golos.commun4j.model

import io.golos.commun4j.sharedmodel.GolosEosError

fun GolosEosError.hasBalanceDoesNotExistError(): Boolean = error
        ?.details
        ?.any {
            it.message?.let { message ->
                message.contains("balance does not exist", true) ||
                        message.contains("balance of from not opened", true)
            } ?: false
        } ?: false