import io.golos.commun4j.BuildConfig
import io.golos.commun4j.model.AuthType
import io.golos.commun4j.utils.AuthUtils
import io.golos.commun4j.sharedmodel.Either
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.*

class RegistrationTest {
    private val client = getClient(setActiveUser = false, authInServices = false)
    val unExistingPhone = generatePhone()
    private val pass = BuildConfig.PHONE_REG_KEY

    @Before
    fun before() {
        client.unAuth()
    }

    @Test
    fun testGetState() {
        val state = client.getRegistrationState(null, "+773217337584")

        assertTrue(state is Either.Success)
    }

    @Test
    fun testAccCreationThroughGate() {
        val accName = "vasyan_rulez-az99"

        val firstStepSuccess = client.firstUserRegistrationStep("any12", unExistingPhone, pass)

        assertTrue(firstStepSuccess is Either.Success)

        println(firstStepSuccess)


        val secondStep = client.verifyPhoneForUserRegistration(unExistingPhone, 1234)

        assertTrue(secondStep is Either.Success)

        println(secondStep)

        val thirdStep = client.setVerifiedUserName(accName, unExistingPhone)

        assertTrue(thirdStep is Either.Success)

        println(thirdStep)

        val keys = AuthUtils.generatePublicWiFs(accName, generatePass(), AuthType.values())

        val lastStep = client.writeUserToBlockChain(accName, keys[AuthType.OWNER]!!,
                keys[AuthType.ACTIVE]!!,
                keys[AuthType.POSTING]!!,
                keys[AuthType.MEMO]!!)

        assertTrue(lastStep is Either.Success)

        assertNotNull(lastStep.getOrThrow().userId)
        assertNotNull(lastStep.getOrThrow().username)

        println(lastStep)

    }
}

fun generatePhone(): String {
    val sb = StringBuilder("+7")
    (0..10).forEach {
        sb.append((Math.random() * 10).toInt())
    }
    return sb.toString()
}

fun generatePass() = (UUID.randomUUID().toString() + UUID.randomUUID().toString()).replace("-", "")