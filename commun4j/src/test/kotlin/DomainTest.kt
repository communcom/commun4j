import io.golos.commun4j.BuildConfig
import io.golos.commun4j.sharedmodel.Either
import junit.framework.Assert.assertTrue
import org.junit.Test
import java.util.*

class DomainTest {
    private val client = getClient()

    @Test
    fun setUserName() {
        val glsCreatorKey = BuildConfig.CREATE_KEY

        val newAcc = account(client.config.toConfigType())
        val result = client.newUserName("gls".toCyberName(),
                UUID.randomUUID().toString().replace("-", ""),
                glsCreatorKey,
                newAcc.first)

        assertTrue(result is Either.Success)
    }
}