import io.golos.commun4j.BuildConfig
import io.golos.commun4j.model.AuthType
import io.golos.commun4j.sharedmodel.Commun4jConfig
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.Either
import io.golos.commun4j.utils.AuthUtils
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*


class AccountCreationTest {


    @Test
    fun testAccountCreation() {
        val client = getClient()
        val pass = UUID.randomUUID().toString()
        val newUser = generateRandomCommunName()
        //val accCreationResult = client
          //      .createAccount(newUser, pass, CyberName("gls"), eosCreateKey)

        //assertTrue("account creation failure on main net for user $newUser", "sdf" is Either.Success)

    }

    @Test
    fun createAccountAndPrintIt() {
        val client = getClient()
        val pass = UUID.randomUUID().toString()
        val newUser = generateRandomCommunName()
        val activeKey = AuthUtils.generatePrivateWiFs(newUser, pass, arrayOf(AuthType.ACTIVE))[AuthType.ACTIVE]!!

      //  val accCreationResult = client.createAccount(newUser, pass, "gls".toCyberName(), eosCreateKey)

      //  assertTrue("account creation failure on main net for user $newUser", "sdg" is Either.Success)

        print("name = $newUser activeKey = $activeKey")

    }

    companion object {
        private const val eosCreateKey = BuildConfig.CREATE_KEY

        fun createNewAccount(forConfig: Commun4jConfig): Pair<CyberName, String> {
            val client = io.golos.commun4j.Commun4j(forConfig)
            val pass = UUID.randomUUID().toString()
            val newUser = generateRandomCommunName()

          //  client.createAccount(newUser, pass, "gls".toCyberName(), eosCreateKey) as Either.Success

            return Pair(CyberName(newUser), AuthUtils.generatePrivateWiFs(newUser, pass, arrayOf(AuthType.ACTIVE))[AuthType.ACTIVE]!!)
        }
    }
}