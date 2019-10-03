package io.golos.commun4j.model

enum class AuthType { /** The owner key type  */
OWNER,
    /** The active key type  */
    ACTIVE,
    /** The memo key type  */
    MEMO,
    /** The posting key type  */
    POSTING;


    override fun toString(): String {
        return when (this) {
            ACTIVE -> "active"
            MEMO -> "memo"
            OWNER -> "owner"
            POSTING -> "posting"
        }
    }
}