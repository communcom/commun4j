package io.golos.commun4j.services.model

import io.golos.commun4j.sharedmodel.CyberName

data class WriteToBlockChainStepResult(val username: String, val userId: CyberName, val currentState: UserRegistrationState)
