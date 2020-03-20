package io.golos.commun4j.services.model

enum class UserRegistrationState {
    REGISTERED,
    FIRST_STEP,
    VERIFY,
    CREATE_IDENTITY,
    SET_USER_NAME,
    TO_BLOCK_CHAIN;
}