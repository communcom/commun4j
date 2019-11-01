package io.golos.commun4j.services

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.AbiBinaryGenTransactionWriter
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAbi
import io.golos.commun4j.http.rpc.SocketClientImpl
import io.golos.commun4j.http.rpc.model.ApiResponseError
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.model.*
import io.golos.commun4j.services.model.*
import io.golos.commun4j.sharedmodel.*
import io.golos.commun4j.utils.*
import java.math.BigInteger
import java.util.*

private enum class ServicesGateMethods {
    GET_FEED, GET_POST, GET_COMMENT, GET_COMMENTS, GET_USER_METADATA, GET_SECRET, AUTH, GET_EMBED,
    GET_REGISTRATION_STATE, REG_FIRST_STEP, REG_VERIFY_PHONE, REG_SET_USER_NAME, REG_WRITE_TO_BLOCKCHAIN,
    REG_RESEND_SMS, WAIT_BLOCK, WAIT_FOR_TRANSACTION, PUSH_SUBSCRIBE, PUSH_UNSUBSCRIBE, GET_NOTIFS_HISTORY, MARK_VIEWED,
    GET_UNREAD_COUNT, MARK_VIEWED_ALL, SET_SETTINGS, GET_SETTINGS, GET_SUBSCRIPTIONS, GET_SUBSCRIBERS,
    RESOLVE_USERNAME, PROVIDE_BANDWIDTH, GET_COMMUNITIES, GET_COMMUNITIY, GET_POSTS, GET_BALANCE, GET_TRANSFER_HISTORY, GET_TOKENS_INFO;

    override fun toString(): String {
        return when (this) {
            GET_FEED -> "content.getFeed"
            GET_POST -> "content.getPost"
            GET_POSTS -> "content.getPosts"
            GET_COMMENT -> "content.getComment"
            GET_COMMENTS -> "content.getComments"
            WAIT_BLOCK -> "content.waitForBlock"
            WAIT_FOR_TRANSACTION -> "content.waitForTransaction"
            GET_USER_METADATA -> "content.getProfile"
            GET_SECRET -> "auth.generateSecret"
            GET_EMBED -> "frame.getEmbed"
            AUTH -> "auth.authorize"
            GET_REGISTRATION_STATE -> "registration.getState"
            REG_FIRST_STEP -> "registration.firstStep"
            REG_VERIFY_PHONE -> "registration.verify"
            REG_SET_USER_NAME -> "registration.setUsername"
            REG_WRITE_TO_BLOCKCHAIN -> "registration.toBlockChain"
            REG_RESEND_SMS -> "registration.resendSmsCode"
            PUSH_SUBSCRIBE -> "push.notifyOn"
            PUSH_UNSUBSCRIBE -> "push.notifyOff"
            GET_NOTIFS_HISTORY -> "push.history"
            MARK_VIEWED -> "notify.markAsViewed"
            GET_UNREAD_COUNT -> "push.historyFresh"
            MARK_VIEWED_ALL -> "notify.markAllAsViewed"
            SET_SETTINGS -> "options.set"
            GET_SETTINGS -> "options.get"
            GET_SUBSCRIPTIONS -> "content.getSubscriptions"
            GET_SUBSCRIBERS -> "content.getSubscribers"
            RESOLVE_USERNAME -> "content.resolveProfile"
            PROVIDE_BANDWIDTH -> "bandwidth.provide"
            GET_COMMUNITIES -> "content.getCommunities"
            GET_COMMUNITIY -> "content.getCommunity"
            GET_BALANCE -> "wallet.getBalance"
            GET_TRANSFER_HISTORY -> "wallet.getTransferHistory"
            GET_TOKENS_INFO -> "wallet.getTokensInfo"
        }
    }
}


internal class CyberServicesApiService @JvmOverloads constructor(
        private val config: Commun4jConfig,
        private val moshi: Moshi = Moshi.Builder()
                .add(Date::class.java, Rfc3339DateJsonAdapter())
                .add(BigInteger::class.java, BigIntegerAdapter())
                .add(CyberName::class.java, CyberNameAdapter())
                .add(UserRegistrationState::class.java, UserRegistrationStateAdapter())
                .add(RegistrationStrategy::class.java, UserRegistrationStrategyAdapter())
                .add(
                        PolymorphicJsonAdapterFactory.of(Content::class.java, "type")
                                .withSubtype(Paragraph::class.java, "paragraph")
                                .withSubtype(Attachments::class.java, "attachments")
                )
                .add(EventType::class.java, EventTypeAdapter())
                .add(CyberAsset::class.java, CyberAssetAdapter())
                .add(CyberSymbolCode::class.java, CyberSymbolCodeAdapter())
                .add(CyberName::class.java, CyberNameAdapter())
                .add(ServiceSettingsLanguage::class.java, ServiceSettingsLanguageAdapter())
                .add(EventsAdapter())
                .add(ToStringAdaptper())
                .add(KotlinJsonAdapterFactory())
                .build(),
        private val apiClient: io.golos.commun4j.http.rpc.SocketClient = SocketClientImpl(
                config.servicesUrl,
                moshi,
                config.readTimeoutInSeconds,
                config.logLevel,
                config.socketLogger)
) : ApiService {

    override fun getAuthSecret(): Either<AuthSecret, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.GET_SECRET.toString(),
                GetSecretRequest(), AuthSecret::class.java
        )
    }

    override fun authWithSecret(user: String,
                                secret: String,
                                signedSecret: String): Either<AuthResult, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.AUTH.toString(),
                ServicesAuthRequest(
                        user,
                        signedSecret,
                        secret
                ), AuthResult::class.java)
    }

    override fun unAuth() {
        apiClient.dropConnection()
    }

    override fun resolveProfile(username: String): Either<ResolvedProfile, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.RESOLVE_USERNAME.toString(),
                ResolveUserNameRequest(
                        username
                ), ResolvedProfile::class.java)
    }

    override fun getCommunitiesList(name: String, offset: Int, limit: Int) = apiClient.send(
            ServicesGateMethods.GET_COMMUNITIES.toString(),
            hashMapOf<Any, Any>("userId" to name, "offset" to offset, "limit" to limit),
            GetCommunitiesResponse::class.java)

    override fun getCommunity(communityId: String) = apiClient.send(
            ServicesGateMethods.GET_COMMUNITIY.toString(),
            hashMapOf(
                    "communityId" to communityId),
            GetCommunitiesItem::class.java)


    override fun <T : Any> pushTransactionWithProvidedBandwidth(chainId: String,
                                                                transactionAbi: TransactionAbi,
                                                                signature: String,
                                                                traceType: Class<T>): Either<TransactionCommitted<T>, GolosEosError> {
        val packedTransactionHex =
                AbiBinaryGenTransactionWriter(CompressionType.NONE)
                        .squishTransactionAbi(transactionAbi, false)
                        .toHex()


        val body = PushTransactionWithProvidedBandwidth(
                TransactionBody(
                        listOf(signature),
                        packedTransactionHex)
                , chainId)

        val response = apiClient
                .send(ServicesGateMethods.PROVIDE_BANDWIDTH.toString(), body, Any::class.java)

        if (response is Either.Failure) return Either
                .Failure(GolosEosError(response.value.error.code.toInt(),
                        response.value.error.message,
                        moshi.adapter(GolosEosError.Error::class.java).fromJsonValue(response.value.error.error)))

        val successResult = (response as Either.Success).value

        return try {

            try {
                val type = Types.newParameterizedType(TransactionCommitted::class.java, traceType)
                val value = moshi
                        .adapter<TransactionCommitted<T>>(type)
                        .fromJsonValue(successResult)!!

                Either.Success(value.copy(
                        resolvedResponse = value.processed
                                .action_traces
                                .map {
                                    val result = try {
                                        moshi.adapter(traceType).lenient().fromJsonValue(it.act.data)
                                    } catch (ignored: JsonDataException) {
                                        null
                                    }
                                    result
                                }
                                .filterNotNull()
                                .firstOrNull()
                ))
            } catch (e: RuntimeException) {
                val type = Types.newParameterizedType(TransactionCommitted::class.java, Any::class.java)
                val value = moshi
                        .adapter<TransactionCommitted<T>>(type)
                        .fromJsonValue(successResult)!!

                Either.Success(value)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Either.Failure(moshi.adapter(GolosEosError::class.java).fromJsonValue(successResult)!!)
        }
    }

    override fun getPost(userId: CyberName,
                         communityId: String,
                         permlink: String
    ): Either<CyberDiscussion, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.GET_POST.toString(),
                DiscussionRequests(userId.name, permlink, communityId),
                CyberDiscussion::class.java
        )
    }

    override fun getPostRaw(userId: CyberName, communityId: String, permlink: String): Either<CyberDiscussionRaw, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.GET_POST.toString(),
                DiscussionRequests(userId.name, permlink, communityId),
                CyberDiscussionRaw::class.java
        )
    }

    override fun getPosts(userId: String?,
                          communityId: String?,
                          communityAlias: String?,
                          allowNsfw: Boolean?,
                          type: String?,
                          sortBy: String?,
                          timeframe: String?,
                          limit: Int?,
                          offset: Int?): Either<GetDiscussionsResult, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.GET_POSTS.toString(),
                GetPostsRequest(userId, communityId, communityAlias,
                        allowNsfw, type, sortBy, timeframe, limit, offset),
                GetDiscussionsResult::class.java
        )
    }

    override fun getPostsRaw(userId: String?,
                             communityId: String?,
                             communityAlias: String?,
                             allowNsfw: Boolean?,
                             type: String?,
                             sortBy: String?,
                             timeframe: String?,
                             limit: Int?,
                             offset: Int?): Either<GetDiscussionsResultRaw, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.GET_POSTS.toString(),
                GetPostsRequest(userId, communityId, communityAlias,
                        allowNsfw, type, sortBy, timeframe, limit, offset),
                GetDiscussionsResultRaw::class.java
        )
    }

    override fun getBalance(name: String): Either<UserBalance, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.GET_BALANCE.toString(),
                mapOf<String, Any>("userId" to name),
                UserBalance::class.java
        )
    }

    override fun getTransferHistory(userId: String, direction: String?, sequenceKey: String?, limit: Int?): Either<GetTransferHistoryResponse, ApiResponseError> {
        return apiClient.send(ServicesGateMethods.GET_TRANSFER_HISTORY.toString(),
                GetTransferHistoryRequest(userId, direction, sequenceKey, limit),
                GetTransferHistoryResponse::class.java)
    }

    override fun getTokensInfo(list: List<String>): Either<GetTokensInfoResponse, ApiResponseError> {
        return apiClient.send(ServicesGateMethods.GET_TOKENS_INFO.toString(),
                mapOf("tokens" to list),
                GetTokensInfoResponse::class.java)
    }

    override fun waitBlock(blockNum: Long): Either<ResultOk, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.WAIT_BLOCK.toString(),
                WaitRequest(blockNum, null),
                ResultOk::class.java
        )
    }

    override fun waitForTransaction(transactionId: String): Either<ResultOk, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.WAIT_FOR_TRANSACTION.toString(),
                WaitRequest(null, transactionId),
                ResultOk::class.java
        )
    }

    override fun getComment(
            userId: String?,
            permlink: String,
            parsingType: ContentParsingType,
            username: String?,
            app: String
    ): Either<CyberDiscussion, ApiResponseError> {

        return apiClient.send(
                ServicesGateMethods.GET_COMMENT.toString(), DiscussionRequests(
                userId!!,
                permlink,
                ""
        ), CyberDiscussion::class.java)
    }

    override fun getComments(
            sort: FeedSort?,
            sequenceKey: String?,
            limit: Int?, origin: CommentsOrigin?,
            parsingType: ContentParsingType,
            userId: String?,
            permlink: String?,
            username: String?,
            appName: String
    ): Either<DiscussionsResult, ApiResponseError> {


        return apiClient.send(
                ServicesGateMethods.GET_COMMENTS.toString(),
                CommentsRequest(
                        sort.toString(),
                        sequenceKey,
                        limit,
                        parsingType.asContentType(),
                        origin.toString(),
                        userId,
                        permlink,
                        username,
                        appName
                ), DiscussionsResult::class.java
        )
    }

    override fun getSubscriptions(ofUser: CyberName,
                                  limit: Int,
                                  type: SubscriptionType,
                                  sequenceKey: String?,
                                  appName: String): Either<SubscriptionsResponse, ApiResponseError> {
        return apiClient.send(ServicesGateMethods.GET_SUBSCRIPTIONS.toString(),
                SubscriptionsRequest(ofUser, limit, type.toString(), sequenceKey, appName),
                SubscriptionsResponse::class.java)
    }

    override fun getSubscribers(ofUser: CyberName, limit: Int, type: SubscriptionType,
                                sequenceKey: String?, appName: String): Either<SubscribersResponse, ApiResponseError> {
        return apiClient.send(ServicesGateMethods.GET_SUBSCRIBERS.toString(),
                SubscribersRequest(ofUser, limit, type.toString(), sequenceKey, appName), SubscribersResponse::class.java)
    }

    override fun getIframelyEmbed(forLink: String): Either<IFramelyEmbedResult, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.GET_EMBED.toString(),
                EmbedRequest(EmbedService.IFRAMELY.toString(), forLink), IFramelyEmbedResult::class.java
        )
    }

    override fun getOEmdedEmbed(forLink: String): Either<OEmbedResult, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.GET_EMBED.toString(),
                EmbedRequest(EmbedService.OEMBED.toString(), forLink), OEmbedResult::class.java
        )
    }

    override fun getProfile(userId: String?, username: String?): Either<GetProfileResult, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.GET_USER_METADATA.toString(),
                UserMetaDataRequest(userId, username), GetProfileResult::class.java
        )
    }

    override fun getRegistrationStateOf(
            userId: String?,
            phone: String?
    ): Either<UserRegistrationStateResult, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.GET_REGISTRATION_STATE.toString(),
                RegistrationStateRequest(userId, phone), UserRegistrationStateResult::class.java
        )
    }

    override fun firstUserRegistrationStep(
            captcha: String?,
            phone: String,
            testingPass: String?
    ): Either<FirstRegistrationStepResult, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.REG_FIRST_STEP.toString(),
                FirstRegistrationStepRequest(captcha, phone, testingPass), FirstRegistrationStepResult::class.java
        )
    }

    override fun verifyPhoneForUserRegistration(phone: String, code: Int): Either<VerifyStepResult, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.REG_VERIFY_PHONE.toString(),
                VerifyPhoneRequest(phone, code), VerifyStepResult::class.java
        )
    }

    override fun setVerifiedUserName(user: String, phone: String): Either<SetUserNameStepResult, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.REG_SET_USER_NAME.toString(),
                RegistrationStateRequest(user, phone), SetUserNameStepResult::class.java
        )
    }

    override fun writeUserToBlockchain(
            phone: String,
            userId: String,
            userName: String,
            owner: String,
            active: String
    ): Either<WriteToBlockChainStepResult, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.REG_WRITE_TO_BLOCKCHAIN.toString(),
                WriteUserToBlockchainRequest(phone, userName, userId, owner, active),
                WriteToBlockChainStepResult::class.java
        )
    }

    override fun resendSmsCode(name: String?, phone: String?): Either<ResultOk, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.REG_RESEND_SMS.toString(),
                ResendUserSmsRequest(name, phone), ResultOk::class.java
        )
    }

    override fun subscribeOnMobilePushNotifications(deviceId: String,
                                                    appName: String,
                                                    fcmToken: String): Either<ResultOk, ApiResponseError> {

        val request = PushSubscibeRequest(fcmToken, deviceId, appName)
        return apiClient.send(
                ServicesGateMethods.PUSH_SUBSCRIBE.toString(),
                request, ResultOk::class.java
        )
    }

    override fun unSubscribeOnNotifications(userId: String,
                                            deviceId: String,
                                            appName: String): Either<ResultOk, ApiResponseError> {

        val request = PushUnSubscibeRequest(userId, deviceId, appName)
        return apiClient.send(
                ServicesGateMethods.PUSH_UNSUBSCRIBE.toString(),
                request, ResultOk::class.java
        )
    }

    override fun setNotificationSettings(deviceId: String,
                                         app: String,
                                         newBasicSettings: Any?,
                                         newWebNotifySettings: WebShowSettings?,
                                         newMobilePushSettings: MobileShowSettings?): Either<ResultOk, ApiResponseError> {

        val request = UserSettings(deviceId, app, newBasicSettings, newWebNotifySettings, newMobilePushSettings)

        return apiClient.send(
                ServicesGateMethods.SET_SETTINGS.toString(),
                request, ResultOk::class.java
        )
    }

    override fun getNotificationSettings(deviceId: String, app: String): Either<UserSettings, ApiResponseError> {

        val request = ServicesSettingsRequest(deviceId, app)

        return apiClient.send(
                ServicesGateMethods.GET_SETTINGS.toString(),
                request, UserSettings::class.java
        )
    }

    override fun getEvents(userProfile: String,
                           appName: String,
                           afterId: String?,
                           limit: Int?,
                           markAsViewed: Boolean?,
                           freshOnly: Boolean?,
                           types: List<EventType>): Either<EventsData, ApiResponseError> {

        val request = EventsRequest(userProfile, appName, afterId, limit, types, markAsViewed, freshOnly)

        return apiClient.send(
                ServicesGateMethods.GET_NOTIFS_HISTORY.toString(),
                request, EventsData::class.java
        )
    }

    override fun markEventsAsRead(ids: List<String>, appName: String): Either<ResultOk, ApiResponseError> {

        val request = MarkAsReadRequest(ids, appName)
        return apiClient.send(ServicesGateMethods.MARK_VIEWED.toString(), request, ResultOk::class.java)
    }

    override fun markAllEventsAsRead(appName: String): Either<ResultOk, ApiResponseError> {

        return apiClient.send(ServicesGateMethods.MARK_VIEWED_ALL.toString(),
                MarkAllReadRequest(appName),
                ResultOk::class.java)
    }

    override fun getUnreadCount(profileId: String, appName: String): Either<FreshResult, ApiResponseError> {

        val request = GetUnreadCountRequest(profileId, appName)

        return apiClient.send(ServicesGateMethods.GET_UNREAD_COUNT.toString(), request, FreshResult::class.java)
    }

    override fun shutDown() {
        apiClient.dropConnection()
    }

    private fun ContentParsingType.asContentType(): String {
        return when (this) {
            ContentParsingType.WEB -> "web"
            ContentParsingType.MOBILE -> "mobile"
            ContentParsingType.RAW -> "raw"
        }
    }
}