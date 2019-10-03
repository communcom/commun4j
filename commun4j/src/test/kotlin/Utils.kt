//import com.squareup.moshi.Moshi
//import com.squareup.moshi.Rfc3339DateJsonAdapter
//import io.golos.commun4j.BuildConfig
//import io.golos.commun4j.Commun4j
//import io.golos.commun4j.abi.implementation.BandWidthProvideOption
//import io.golos.commun4j.abi.implementation.gls.publish.CreatemssgGlsPublishAction
//import io.golos.commun4j.abi.implementation.gls.publish.CreatemssgGlsPublishStruct
//import io.golos.commun4j.abi.implementation.gls.publish.MssgidGlsPublishStruct
//import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
//import io.golos.commun4j.core.crypto.EosPrivateKey
//import io.golos.commun4j.core.crypto.signature.PrivateKeySigning
//import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
//import io.golos.commun4j.model.ContentRow
//import io.golos.commun4j.services.model.*
//import io.golos.commun4j.sharedmodel.*
//import io.golos.commun4j.utils.*
//import junit.framework.Assert.assertNotNull
//import junit.framework.Assert.assertTrue
//import org.junit.Before
//import org.junit.Test
//import java.io.File
//import java.math.BigInteger
//import java.nio.charset.Charset
//import java.util.*
//
//class Utils {
//    private lateinit var client: Commun4j
//    private lateinit var secondAccount: kotlin.Pair<CyberName, String>
//
//    @Before
//    fun before() {
//        client = getClient()
//        secondAccount = account(client.config.toConfigType(), true)
//    }
//
//    @Test
//    fun cyberNameTest() {
//        val cyber4j = client
//        val posts = (cyber4j.getUserPosts(
//                client.activeAccountPair.first,
//                null,
//                ContentParsingType.MOBILE,
//                100,
//                FeedSort.SEQUENTIALLY,
//                null,
//                "gls"
//        ) as Either.Success)
//        val firstPost = posts.value.items.first()
//        val second = posts.value.items[1]
//
//        cyber4j.vote(
//                firstPost.contentId.userId.toCyberName(), firstPost.contentId.permlink,
//                (Math.random() * 10_000).toShort()
//        ) as Either.Success
//        cyber4j.vote(
//                firstPost.contentId.userId.toCyberName(), firstPost.contentId.permlink,
//                (Math.random() * -10_000).toShort()
//        ) as Either.Success
//
//        cyber4j.vote(
//                second.contentId.userId.toCyberName(), second.contentId.permlink,
//                (Math.random() * 10_000).toShort()
//        ) as Either.Success
//        cyber4j.vote(
//                second.contentId.userId.toCyberName(), second.contentId.permlink,
//                (Math.random() * -10_000).toShort()
//        ) as Either.Success
//    }
//
//
//    @Test
//    fun deserializeEvents() {
//        val eventsString = (DomainTest::class.java).getResource("/test.json").readText(Charset.defaultCharset())
//
//        assertNotNull(eventsString)
//
//        val moshi = Moshi.Builder().add(Date::class.java, Rfc3339DateJsonAdapter())
//                .add(BigInteger::class.java, BigIntegerAdapter())
//                .add(CyberName::class.java, CyberNameAdapter())
//                .add(UserRegistrationState::class.java, UserRegistrationStateAdapter())
//                .add(RegistrationStrategy::class.java, UserRegistrationStrategyAdapter())
//                .add(ContentRow::class.java, ContentRowAdapter())
//                .add(EventType::class.java, EventTypeAdapter())
//                .add(CyberNameAdapter())
//                .add(ServiceSettingsLanguage::class.java, ServiceSettingsLanguageAdapter())
//                .add(EventsAdapter())
//                .build()
//
//        val events = moshi.adapter<EventsData>(EventsData::class.java).fromJson(eventsString)
//        assertNotNull(events)
//
//        assertTrue(events!!.data.isNotEmpty())
//    }
//
//    @Test
//    fun imageUploadTest() {
//        val img = File((DomainTest::class.java).getResource("/test2.jpg").file!!)
//        assertNotNull(img)
//        val uploadResponse = client.uploadImage(img)
//        assertTrue(uploadResponse is Either.Success)
//        println((uploadResponse as Either.Success).value)
//
//    }
//
//
//    @Test
//    fun withdrawtest() {
//        val result = client.withdraw(client.keyStorage.getActiveAccount(),
//                secondAccount.first, "0.001000 GOLOS", client.keyStorage.activeAccountPair.second)
//        assertTrue(result is Either.Success)
//
//        val stopResult =
//                client.stopWithdraw(client.keyStorage.getActiveAccount(), client.keyStorage.activeAccountPair.second)
//        assertTrue(stopResult is Either.Success)
//    }
//
//    @Test
//    fun delegate() {
//        val result = client.delegate(
//                client.keyStorage.getActiveAccount(),
//                secondAccount.first, "1.000000 GOLOS", 100.toShort(),
//                client.keyStorage.activeAccountPair.second
//        )
//        assertTrue(result is Either.Success)
//
//    }
//
//    @Test
//    fun testResolveName() {
//        val usernameDomain = "lakin-ernesto-phd"
//        val resolvedCanonicalName = client.resolveCanonicalCyberName(usernameDomain,
//                "cyber").getOrThrow()
//        assertNotNull(resolvedCanonicalName.username)
//        assertNotNull(resolvedCanonicalName.userId)
//        assertTrue(resolvedCanonicalName.userId.name != usernameDomain)
//    }
//
//    @Test
//    fun testRawApi() {
//
//        val result: Either<out TransactionCommitted<out CreatemssgGlsPublishStruct>, GolosEosError> = CreatemssgGlsPublishAction(CreatemssgGlsPublishStruct(
//                MssgidGlsPublishStruct(secondAccount.first,
//                        "sdgewtwesgsd2322222511"),
//                MssgidGlsPublishStruct(CyberName(""), ""),
//                emptyList(), 0, true, "title", " nывпывп 25 ывп ывп",
//                "ru", emptyList(), "", null, null)
//        ).push(
//                listOf(TransactionAuthorizationAbi(secondAccount.first.name, "active")),
//                EosPrivateKey(secondAccount.second),
//                Commun4jConfig(BuildConfig.CYBER_DEV_246, BuildConfig.GATE_136),
//                BandWidthProvideOption("gls".toCyberName()),
//                EosPrivateKey(BuildConfig.GOLOSIO_KEY)
//        )
//
//        print(result)
//
//        assertTrue(result is Either.Success)
//    }
//
//    @Test
//    fun testProvideBw() {
//        val user = "bscilhhworde"
//        val key = EosPrivateKey("5Jcc5ajYXdijWn5ew7QnH5xF7Ec6jBUV6jjh4VWiigjLwNhs1fg")
//        val result = CreatemssgGlsPublishAction(CreatemssgGlsPublishStruct(
//                MssgidGlsPublishStruct(CyberName(user),
//                        UUID.randomUUID().toString()),
//                MssgidGlsPublishStruct(CyberName(""), ""),
//                emptyList(), 0, true, "title", " nывпывп 25 ывп ывп",
//                "ru", emptyList(), "", null, null)
//        ).createSignedTransactionForProvideBw(
//                listOf(TransactionAuthorizationAbi(user, "active")),
//                key,
//                Commun4jConfig(BuildConfig.CYBER_DEV_246, BuildConfig.GATE_136),
//                BandWidthProvideOption("gls".toCyberName()))
//
//        result as Either.Success
//
//        val secret = client.getAuthSecret().getOrThrow().secret
//
//        client.authWithSecret(user,
//                secret,
//                PrivateKeySigning().sign(secret.toByteArray(), key))
//
//        val pushResult = client.pushTransactionWithProvidedBandwidth(
//                result.getOrThrow().usedChainInfo.chain_id,
//                result.getOrThrow().transaction,
//                result.getOrThrow().signedTransactionSignatures.first(),
//                CreatemssgGlsPublishStruct::class.java
//        )
//
//        println(pushResult)
//
//        pushResult as Either.Success
//    }
//}