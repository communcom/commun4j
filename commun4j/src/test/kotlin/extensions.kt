import io.golos.commun4j.Commun4j
import io.golos.commun4j.KeyStorage
import io.golos.commun4j.model.AuthType
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.Either

internal fun String.toCyberName() = CyberName(this)

fun generateRandomCommunName(): String {
    val builder = StringBuilder()
    (0..11).forEach {
        builder.append((Math.random() * 25).toChar() + 97)
    }
    return builder.toString()
}

val KeyStorage.activeAccountPair: Pair<CyberName, String>
    get() = Pair(getActiveAccount(), this.getActiveAccountKeys().find { it.first == AuthType.ACTIVE }!!.second)

val Commun4j.activeAccountPair
    get() = keyStorage.activeAccountPair

fun <S, F> Either<S, F>.getOrThrow(): S = (this as Either.Success).value

fun Commun4j.setActiveAccount(pair: Pair<CyberName, String>) {
    keyStorage.addAccountKeys(pair.first,
            setOf(io.golos.commun4j.utils.Pair(AuthType.ACTIVE, pair.second)))
}