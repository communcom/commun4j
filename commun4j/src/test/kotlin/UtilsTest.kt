import com.squareup.moshi.Moshi
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.model.BandWidthRequest
import io.golos.commun4j.model.ClientAuthRequest
import io.golos.commun4j.model.hasBalanceDoesNotExistError
import io.golos.commun4j.model.resolveActions
import io.golos.commun4j.sharedmodel.CheckSum256
import io.golos.commun4j.sharedmodel.GolosEosError
import org.junit.Assert
import org.junit.Test

class UtilsTest {
    @Test
    fun actionsResolverTests() {
        val action = Moshi.Builder().build().adapter(ActionAbi::class.java).fromJson("{\n" +
                "  \"account\": \"c.gallery\",\n" +
                "  \"name\": \"create\",\n" +
                "  \"authorization\": [\n" +
                "    {\n" +
                "      \"actor\": \"tst2dhcuszkc\",\n" +
                "      \"permission\": \"active\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"data\": \"41434f4d4d554e0080e0c71ab52432ce2438366165653961632d303465622d346639652d613465342d3766363864323261386638310000000000000000002438366165653961632d303465622d346639652d613465342d3766363864323261386638312435303434636232342d386133632d346161352d383534612d3033393034353532396266610000010a00\"\n" +
                "}")!!
        val resolvedActions = resolveActions(listOf(action), BandWidthRequest.bandWidthFromComn, ClientAuthRequest.empty)

        Assert.assertEquals("there must be 3 action, 1 original with 2 providebw", 3, resolvedActions.size)
        Assert.assertEquals("there must be 2 providebw", 2, resolvedActions.filter { it.name == "providebw" }.size)


        val createAction = (resolvedActions.find { it.name == "create" })!!

        Assert.assertEquals("there must be 2 auths", 2, createAction.authorization.size)
        Assert.assertTrue("firs auth is original", createAction.authorization.first() == TransactionAuthorizationAbi("tst2dhcuszkc", "active"))
        Assert.assertEquals("second must be actor must be `c.gallery@clients`", TransactionAuthorizationAbi("c.gallery", "clients"), createAction.authorization[1])
    }


    @Test
    fun testBalanceDoesNotExistParse() {
        val error = Moshi
                .Builder()
                .build()
                .adapter<GolosEosError>(GolosEosError::class.java)
                .fromJson(this::class.java.getResource("/balance_error.json").readText())!!

        println(error)

        Assert.assertTrue(error.hasBalanceDoesNotExistError())
    }


}

