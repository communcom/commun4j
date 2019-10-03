//import io.golos.commun4j.BuildConfig
//import io.golos.commun4j.Commun4j
//import io.golos.commun4j.core.crypto.EosPrivateKey
//import io.golos.commun4j.model.BandWidthRequest
//import io.golos.commun4j.model.BandWidthSource
//import io.golos.commun4j.sharedmodel.CyberName
//import io.golos.commun4j.sharedmodel.Either
//import org.junit.Assert.assertEquals
//import org.junit.Assert.assertTrue
//import org.junit.Before
//import org.junit.Test
//
//class SocialMainTestNetTest {
//
//    lateinit var client: Commun4j
//    private lateinit var secondAcc: kotlin.Pair<CyberName, String>
//
//
//    @Before
//    fun before() {
//        client = getClient()
//        // secondAcc = account(client.config.toConfigType())
//    }
//
//    @Test
//    fun setUserMeta() {
//        val golosBandwidthRequest = BandWidthRequest(BandWidthSource.USING_KEY, "gls".toCyberName(),
//                EosPrivateKey(BuildConfig.GOLOSIO_KEY))
//        val setMetaResult =
//                client.setUserMetadata("типа", "аппа", website = "веб-портал", email = "",
//                        bandWidthRequest = golosBandwidthRequest)
//        assertTrue("meta change fail", setMetaResult is Either.Success)
//        val changedMeta = (setMetaResult as Either.Success).value.resolvedResponse!!.meta
//
//        assertEquals("type set fail", "типа", changedMeta.type)
//        assertEquals("app set fail", "аппа", changedMeta.app)
//        assertEquals("web-site set fail", "веб-портал", changedMeta.website)
//
//        val deleteResult = client.deleteUserMetadata(golosBandwidthRequest)
//
//        assertTrue("meta delete fail", deleteResult is Either.Success)
//
//
//        val newUser = account(client.config.toConfigType())
//
//        val setMetaResultSecond = client.setUserMetadata(newUser.first,
//                newUser.second,
//                "типа1", "аппа1", website = "веб-портал1", bandWidthRequest = golosBandwidthRequest)
//        assertTrue("meta change fail", setMetaResultSecond is Either.Success)
//
//        val changedMetaSecond = (setMetaResultSecond as Either.Success).value.resolvedResponse!!.getMeta
//
//
//        assertEquals("type set fail", "типа1", changedMetaSecond.type)
//        assertEquals("app set fail", "аппа1", changedMetaSecond.app)
//        assertEquals("web-site set fail", "веб-портал1", changedMetaSecond.website)
//
//        val deleteResultSecond =
//                client.deleteUserMetadata(client.activeAccountPair.first, client.activeAccountPair.second, bandWidthRequest = golosBandwidthRequest)
//
//        assertTrue("meta delete fail", deleteResultSecond is Either.Success)
//    }
//
//    @Test
//    fun testPinUnpin() {
//        val golosBandwidthRequest = BandWidthRequest(BandWidthSource.USING_KEY, "gls".toCyberName(),
//                EosPrivateKey(BuildConfig.GOLOSIO_KEY))
//
//        val acc = account(client.config.toConfigType())
//        val pinResult = client.pin(acc.first, bandWidthRequest = golosBandwidthRequest)
//        assertTrue("pin fail", pinResult is Either.Success)
//
//        val unpinResult = client.unPin(acc.first,
//                bandWidthRequest = golosBandwidthRequest)
//        assertTrue("unpin fail", unpinResult is Either.Success)
//
//        val pinResultSecond = client.pin(secondAcc.second, secondAcc.first,
//                client.keyStorage.getActiveAccount(), bandWidthRequest = golosBandwidthRequest)
//        assertTrue("pin fail", pinResultSecond is Either.Success)
//
//        val unPinResultSecond = client.unPin(secondAcc.second, secondAcc.first,
//                client.keyStorage.getActiveAccount(), bandWidthRequest = golosBandwidthRequest)
//        assertTrue("pin fail", unPinResultSecond is Either.Success)
//    }
//
//    @Test
//    fun testBlocking() {
//        val acc = account(client.config.toConfigType())
//        val blockResult = client.block(acc.first)
//
//        assertTrue("user $acc block fail", blockResult is Either.Success)
//
//        val blockResultSecond = client.block(secondAcc.second, secondAcc.first,
//                client.keyStorage.getActiveAccount())
//        assertTrue("pin fail", blockResultSecond is Either.Success)
//
//
//        val unblockResult = client.unBlock(acc.first)
//        assertTrue("user $acc unblock fail", unblockResult is Either.Success)
//
//        val unBlockResultSecond = client.unBlock(secondAcc.second, secondAcc.first,
//                client.keyStorage.getActiveAccount())
//        assertTrue("pin fail", unBlockResultSecond is Either.Success)
//    }
//
//    @Test
//    fun testGetName() {
//        println(client.resolveCanonicalCyberName("joseph.kalu", "gls"))
//    }
//}