package io.golos.commun4j.sharedmodel


data class GolosEosError(
        val code: Int = 0,
        val message: String? = null,
        val error: Error? = null) {


    data class Error(val code: Int = 0,
                     val name: String?,
                     val what: String?,
                     val details: List<ErrorMessage> = emptyList())


    data class ErrorMessage(var message: String?, var file: String?,
                            var line_number: Int, var method: String?)
}


