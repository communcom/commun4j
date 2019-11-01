//import io.golos.commun4j.Commun4j
//import io.golos.commun4j.sharedmodel.Either
//import io.golos.commun4j.utils.StringSigner
//import junit.framework.Assert.assertFalse
//import junit.framework.Assert.assertTrue
//import org.junit.Before
//import org.junit.Test
//
//class ServicesAuthTest {
//    private lateinit var client: Commun4j
//
//    @Before
//    fun before() {
//        client = getClient()
//    }
//
//    @Test
//    fun testAuth() {
//        client.unAuth()
//
//        assertFalse(client.isUserAuthed().getOrThrow())
//
//        val secret = client.getAuthSecret().getOrThrow().secret
//
//        val authResult = client.authWithSecret(client.activeAccountPair.first.name,
//                secret,
//                StringSigner.signString(secret,
//                        client.activeAccountPair.second))
//        assertTrue(authResult is Either.Success)
//
//        assertTrue(client.isUserAuthed().getOrThrow())
//    }
//}
