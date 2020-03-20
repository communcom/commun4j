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
import io.golos.commun4j.http.rpc.RpcServerMessage
import io.golos.commun4j.http.rpc.RpcServerMessageCallback
import io.golos.commun4j.http.rpc.SocketClient
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
    GET_POST, GET_COMMENT, GET_COMMENTS, GET_USER_METADATA, GET_SECRET, AUTH, GET_EMBED,
    GET_REGISTRATION_STATE, REG_FIRST_STEP, REG_VERIFY_PHONE, REG_SET_USER_NAME, REG_WRITE_TO_BLOCKCHAIN,
    REG_RESEND_SMS, WAIT_BLOCK, WAIT_FOR_TRANSACTION, PUSH_SUBSCRIBE, PUSH_UNSUBSCRIBE, GET_NOTIFS_HISTORY, MARK_VIEWED,
    GET_UNREAD_COUNT, MARK_VIEWED_ALL, SET_SETTINGS, GET_SETTINGS, GET_SUBSCRIPTIONS, GET_SUBSCRIBERS,
    RESOLVE_USERNAME, PROVIDE_BANDWIDTH, GET_COMMUNITIES, GET_COMMUNITIY, GET_POSTS, GET_BALANCE, GET_TRANSFER_HISTORY, GET_TOKENS_INFO,
    GET_LEADERS, GET_COMMUNITY_BLACKLIST, GET_BLACKLIST, GET_COMMENT_VOTES, GET_POST_VOTES, GET_NOTIFY_META,
    GET_ENTITY_REPORTS, GET_REPORTS, SUGGEST_NAMES, ONBOARDING_COMMUNITY_SUBSCRIPTION, GET_NOTIFICATIONS,
    GET_NOTIFICATIONS_STATUS, GET_BULK, SUBSCRIBE_NOTIFICATIONS, UN_SUBSCRIBE_NOTIFICATIONS, GET_COIN_BUY_PRICE, GET_COIN_SELL_PRICE,
    GET_CONFIG, SEARCH_QUICK, SEARCH_EXTENDED;

    override fun toString(): String {
        return when (this) {
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
            ONBOARDING_COMMUNITY_SUBSCRIPTION -> "registration.onboardingCommunitySubscriptions"
            REG_RESEND_SMS -> "registration.resendSmsCode"
            PUSH_SUBSCRIBE -> "push.notifyOn"
            PUSH_UNSUBSCRIBE -> "push.notifyOff"
            GET_NOTIFS_HISTORY -> "push.history"
            MARK_VIEWED -> "notify.markAsViewed"
            GET_UNREAD_COUNT -> "push.historyFresh"
            MARK_VIEWED_ALL -> "notifications.markAllAsViewed"
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
            GET_LEADERS -> "content.getLeaders"
            GET_COMMUNITY_BLACKLIST -> "content.getCommunityBlacklist"
            GET_BLACKLIST -> "content.getBlacklist"
            GET_COMMENT_VOTES -> "content.getCommentVotes"
            GET_POST_VOTES -> "content.getPostVotes"
            GET_NOTIFY_META -> "content.getNotifyMeta"
            GET_ENTITY_REPORTS -> "content.getEntityReports"
            GET_REPORTS -> "content.getReportsList"
            SUGGEST_NAMES -> "content.suggestNames"
            GET_NOTIFICATIONS -> "notifications.getNotifications"
            GET_NOTIFICATIONS_STATUS -> "notifications.getStatus"
            GET_BULK -> "rewards.getStateBulk"
            SUBSCRIBE_NOTIFICATIONS -> "notifications.subscribe"
            UN_SUBSCRIBE_NOTIFICATIONS -> "notifications.unsubscribe"
            GET_COIN_BUY_PRICE -> "wallet.getBuyPrice"
            GET_COIN_SELL_PRICE -> "wallet.getSellPrice"
            GET_CONFIG -> "config.getConfig"
            SEARCH_QUICK -> "content.quickSearch"
            SEARCH_EXTENDED -> "content.extendedSearch"
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
                                .withSubtype(ImageContent::class.java, "image")
                                .withSubtype(Attachments::class.java, "attachments")
                                .withSubtype(VideoContent::class.java, "video")
                                .withSubtype(EmbedContent::class.java, "embed")
                                .withSubtype(Website::class.java, "website")
                )
                .add(
                        PolymorphicJsonAdapterFactory.of(Notification::class.java, "eventType")
                                .withSubtype(SubscribeNotification::class.java, "subscribe")
                                .withSubtype(UpvoteNotification::class.java, "upvote")
                                .withSubtype(MentionNotification::class.java, "mention")
                                .withSubtype(ReplyNotification::class.java, "reply")
                                .withSubtype(TransferNotification::class.java, "transfer")
                                .withSubtype(RewardNotification::class.java, "reward")
                )
                .add(
                        PolymorphicJsonAdapterFactory.of(QuickSearchResponseItem::class.java, "type")
                                .withSubtype(QuickSearchCommunityItem::class.java, "community")
                                .withSubtype(QuickSearchProfileItem::class.java, "profile")
                                .withSubtype(QuickSearchCommentItem::class.java, "comment")
                                .withSubtype(QuickSearchPostItem::class.java, "post")
                )
                .add(EventType::class.java, EventTypeAdapter())
                .add(CyberAsset::class.java, CyberAssetAdapter())
                .add(CyberSymbolCode::class.java, CyberSymbolCodeAdapter())
                .add(WalletQuantity::class.java, WalletQuantityAdapter())
                .add(CyberName::class.java, CyberNameAdapter())
                .add(ServiceSettingsLanguage::class.java, ServiceSettingsLanguageAdapter())
                .add(EventsAdapter())
                .add(ToStringAdapter())
                .add(KotlinJsonAdapterFactory())
                .build(),
        serverMessageCallback: RpcServerMessageCallback = object : RpcServerMessageCallback {
            override fun onMessage(message: RpcServerMessage) {

            }
        },
        private val apiClient: SocketClient = SocketClientImpl(
                "${config.servicesUrl}?platform=${config.socketOpenQueryParams.platform}&" +
                        "deviceType=${config.socketOpenQueryParams.deviceType}" +
                        "&clientType=${config.socketOpenQueryParams.clientType}&version=${config.socketOpenQueryParams.version}",
                moshi,
                serverMessageCallback,
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

    override fun getCommunitiesList(type: String?, userId: String?, search: String?, offset: Int?, limit: Int?) = apiClient.send(
            ServicesGateMethods.GET_COMMUNITIES.toString(),
            GetCommunitiesRequest(type, userId, search, offset, limit),
            GetCommunitiesResponse::class.java)

    override fun getCommunity(communityId: String?, communityAlias: String?) = apiClient.send(
            ServicesGateMethods.GET_COMMUNITIY.toString(),
            GetCommunityRequest(communityId, communityAlias),
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

        if (response is Either.Failure) {

            var golosEosError = moshi.adapter(GolosEosError::class.java).fromJsonValue(response.value.error.data)
            if (golosEosError != null) return Either.Failure(golosEosError)

            golosEosError = moshi.adapter(GolosEosError::class.java).fromJsonValue(response.value.error.error)
            if (golosEosError != null) return Either.Failure(golosEosError)

            return Either
                    .Failure(GolosEosError(response.value.error.code.toInt(),
                            response.value.error.message,
                            null))
        }

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

    override fun getLeaders(communityId: String, limit: Int?, skip: Int?, query: String?): Either<LeadersResponse, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.GET_LEADERS.toString(),
                LeadersRequest(communityId, limit, skip, query),
                LeadersResponse::class.java
        )
    }

    override fun getCommunityBlacklist(communityId: String?, communityAlias: String?, offset: Int?, limit: Int?): Either<CommunityBlacklistResponse, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.GET_COMMUNITY_BLACKLIST.toString(),
                CommunityBlacklistRequest(communityId, communityAlias, offset, limit),
                CommunityBlacklistResponse::class.java
        )
    }

    override fun getBlacklistedUsers(userId: CyberName): Either<BlacklistedUsersResponse, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.GET_BLACKLIST.toString(),
                BlacklistRequest(userId, BlacklistType.USER.toString()),
                BlacklistedUsersResponse::class.java
        )
    }

    override fun getBlacklistedCommunities(userId: CyberName): Either<BlacklistedCommunitiesResponse, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.GET_BLACKLIST.toString(),
                BlacklistRequest(userId, BlacklistType.COMMUNITIES.toString()),
                BlacklistedCommunitiesResponse::class.java
        )
    }

    override fun getComment(name: String, communityId: String, permlink: String): Either<CyberComment, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.GET_COMMENT.toString(),
                GetCommentRequest(name, communityId, permlink),
                CyberComment::class.java)
    }

    override fun getCommentRaw(name: String, communityId: String, permlink: String): Either<CyberCommentRaw, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.GET_COMMENT.toString(),
                GetCommentRequest(name, communityId, permlink),
                CyberCommentRaw::class.java)
    }

    override fun getComments(sortBy: String?, offset: Int?, limit: Int?, type: String?, userId: String?,
                             permlink: String?, communityId: String?, communityAlias: String?, parentComment: ParentComment?, resolveNestedComments: Boolean?): Either<GetCommentsResponse, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.GET_COMMENTS.toString(),
                GetCommentsRequest(sortBy, offset, limit, type, userId, permlink, communityId, communityAlias,
                        parentComment, resolveNestedComments),
                GetCommentsResponse::class.java)
    }

    override fun getCommentsRaw(sortBy: String?, offset: Int?, limit: Int?, type: String?, userId: String?, permlink: String?, communityId: String?, communityAlias: String?, parentComment: ParentComment?, resolveNestedComments: Boolean?): Either<GetCommentsResponseRaw, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.GET_COMMENTS.toString(),
                GetCommentsRequest(sortBy, offset, limit, type, userId, permlink, communityId, communityAlias,
                        parentComment, resolveNestedComments),
                GetCommentsResponseRaw::class.java)
    }

    override fun getUserSubscriptions(ofUser: CyberName, limit: Int?, offset: Int?): Either<UserSubscriptionsResponse, ApiResponseError> {
        return apiClient.send(ServicesGateMethods.GET_SUBSCRIPTIONS.toString(),
                SubscriptionsRequest(ofUser, SubscriptionType.USER.toString(), limit, offset),
                UserSubscriptionsResponse::class.java)
    }

    override fun getCommunitySubscriptions(ofUser: CyberName, limit: Int?, offset: Int?): Either<CommunitySubscriptionsResponse, ApiResponseError> {
        return apiClient.send(ServicesGateMethods.GET_SUBSCRIPTIONS.toString(),
                SubscriptionsRequest(ofUser, SubscriptionType.COMMUNITY.toString(), limit, offset),
                CommunitySubscriptionsResponse::class.java)
    }

    override fun getSubscribers(userId: CyberName?,
                                communityId: String?,
                                limit: Int?,
                                offset: Int?): Either<SubscribedUsersResponse, ApiResponseError> {
        return apiClient.send(ServicesGateMethods.GET_SUBSCRIBERS.toString(),
                SubscribersRequest(userId, communityId, limit, offset), SubscribedUsersResponse::class.java)
    }

    override fun getReports(communityIds: List<String>?,
                            status: ReportsRequestStatus?,
                            contentType: ReportRequestContentType?,
                            sortBy: ReportsRequestTimeSort?,
                            limit: Int?,
                            offset: Int?): Either<GetReportsResponse, ApiResponseError> {

        return apiClient.send(ServicesGateMethods.GET_REPORTS.toString(),
                GetReportsRequest(communityIds, status?.toString(), contentType?.toString(),
                        sortBy?.toString(), limit, offset), GetReportsResponse::class.java)
    }

    override fun getReportsRaw(communityIds: List<String>?,
                               status: ReportsRequestStatus?,
                               contentType: ReportRequestContentType?,
                               sortBy: ReportsRequestTimeSort?,
                               limit: Int?,
                               offset: Int?): Either<GetReportsResponseRaw, ApiResponseError> {

        return apiClient.send(ServicesGateMethods.GET_REPORTS.toString(),
                GetReportsRequest(communityIds, status?.toString(), contentType?.toString(),
                        sortBy?.toString(), limit, offset), GetReportsResponseRaw::class.java)
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
            phone: String?,
            identity: String?
    ): Either<UserRegistrationStateResult, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.GET_REGISTRATION_STATE.toString(),
                RegistrationStateRequest(userId, phone, identity), UserRegistrationStateResult::class.java
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

    override fun setVerifiedUserName(user: String, phone: String?, identity: String?): Either<SetUserNameStepResult, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.REG_SET_USER_NAME.toString(),
                SetVerifiedUserNameRequest(user, phone, identity), SetUserNameStepResult::class.java
        )
    }

    override fun writeUserToBlockchain(
            phone: String?,
            identity: String?,
            userId: String,
            userName: String,
            owner: String,
            active: String
    ): Either<WriteToBlockChainStepResult, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.REG_WRITE_TO_BLOCKCHAIN.toString(),
                WriteUserToBlockchainRequest(phone, identity, userName, userId, owner, active),
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

    override fun suggestNames(text: String): Either<SuggestNameResponse, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.SUGGEST_NAMES.toString(),
                SuggestNameRequest(text), SuggestNameResponse::class.java
        )
    }

    override fun onBoardingCommunitySubscriptions(name: String, communityIds: List<String>): Either<ResultOk, ApiResponseError> {
        return apiClient.send(
                ServicesGateMethods.ONBOARDING_COMMUNITY_SUBSCRIPTION.toString(),
                mapOf(
                        "userId" to name,
                        "communityIds" to communityIds
                ), ResultOk::class.java
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

    override fun getUnreadCount(profileId: String, appName: String): Either<FreshResult, ApiResponseError> {

        val request = GetUnreadCountRequest(profileId, appName)

        return apiClient.send(ServicesGateMethods.GET_UNREAD_COUNT.toString(), request, FreshResult::class.java)
    }

    override fun getNotifications(limit: Int?, beforeThan: String?, filter: List<GetNotificationsFilter>?): Either<GetNotificationsResponse, ApiResponseError> {

        val request = GetNotificationsRequest(limit, beforeThan, filter?.map { it.toString() })

        return apiClient.send(ServicesGateMethods.GET_NOTIFICATIONS.toString(), request, GetNotificationsResponse::class.java)

    }

    override fun getNotificationsSafe(limit: Int?, beforeThan: String?, filter: List<GetNotificationsFilter>?): Either<GetNotificationsResponse, ApiResponseError> {

        val request = GetNotificationsRequest(limit, beforeThan, filter?.map { it.toString() })

        val resp = apiClient.send(ServicesGateMethods.GET_NOTIFICATIONS.toString(), request, GetNotificationsRaw::class.java)
        val notificationsAdapter = moshi.adapter<Notification>(Notification::class.java)

        @Suppress("UNCHECKED_CAST")
        return if (resp is Either.Failure) resp as Either.Failure<GetNotificationsResponse, ApiResponseError>
        else (resp as Either.Success)
                .value
                .mapNotNull { rawNotification ->
                    try {
                        notificationsAdapter.fromJsonValue(rawNotification)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        null
                    }
                }
                .run {
                    Either.Success<GetNotificationsResponse, ApiResponseError>(GetNotificationsResponse(this, resp.value.lastNotificationTimestamp))
                }
    }

    override fun getNotificationsStatus(): Either<GetNotificationStatusResponse, ApiResponseError> {

        val request = GetNotificationsStatusRequest()

        return apiClient.send(ServicesGateMethods.GET_NOTIFICATIONS_STATUS.toString(), request, GetNotificationStatusResponse::class.java)

    }

    override fun markAllNotificationAsViewed(until: String): Either<ResultOk, ApiResponseError> {

        val request = MarkAllNotificationsAsViewedRequest(until)

        return apiClient.send(ServicesGateMethods.MARK_VIEWED_ALL.toString(), request, ResultOk::class.java)

    }

    override fun getStateBulk(posts: List<UserAndPermlinkPair>): Either<GetStateBulkResponse, ApiResponseError> {

        val request = GetStateBulkRequest(posts)

        val resp = apiClient.send(ServicesGateMethods.GET_BULK.toString(), request, Map::class.java)
        @Suppress("UNCHECKED_CAST")
        if (resp is Either.Failure) return resp as Either<GetStateBulkResponse, ApiResponseError>

        val type = Types.newParameterizedType(Map::class.java, String::class.java, List::class.java, Map::class.java)

        val value = (resp as Either.Success).value

        val itemAdapter = moshi.adapter<GetStateBulkResponseItem>(GetStateBulkResponseItem::class.java)

        val result = moshi
                .adapter<Map<String, List<Map<*, *>>>>(type)
                .fromJsonValue(value)!!
                .mapValues { mapEntry ->
                    mapEntry.value.map { itemAdapter.fromJsonValue(it)!! }
                }

        return Either.Success(result)
    }

    override fun subscribeOnNotifications(): Either<ResultOk, ApiResponseError> {
        return apiClient.send(ServicesGateMethods.SUBSCRIBE_NOTIFICATIONS.toString(), SubscribeOnNotifications(), ResultOk::class.java)
    }

    override fun unSubscribeFromNotifications(): Either<ResultOk, ApiResponseError> {
        return apiClient.send(ServicesGateMethods.UN_SUBSCRIBE_NOTIFICATIONS.toString(), UnSubscribeFromNotifications(), ResultOk::class.java)
    }

    override fun getWalletBalance(userId: CyberName): Either<GetUserBalanceResponse, ApiResponseError> {
        val request = GetUserBalanceRequest(userId.name)
        return apiClient.send(ServicesGateMethods.GET_BALANCE.toString(), request, GetUserBalanceResponse::class.java)
    }

    override fun getTransferHistory(userId: CyberName, direction: TransferHistoryDirection?, transferType: TransferHistoryTransferType?,
                                    symbol: CyberSymbolCode?, rewards: String?, limit: Int?, offset: Int?): Either<GetTransferHistoryResponse, ApiResponseError> {
        val request = GetTransferHistoryRequest(userId, direction, transferType, symbol, rewards, limit, offset)
        return apiClient.send(ServicesGateMethods.GET_TRANSFER_HISTORY.toString(), request, GetTransferHistoryResponse::class.java)
    }

    override fun getBuyPrice(pointSymbol: CyberSymbolCode, quantity: WalletQuantity): Either<GetWalletBuyPriceResponse, ApiResponseError> {
        val request = GetWalletBuyPriceRequest(pointSymbol, quantity)
        return apiClient.send(ServicesGateMethods.GET_COIN_BUY_PRICE.toString(), request, GetWalletBuyPriceResponse::class.java)
    }

    override fun getSellPrice(quantity: WalletQuantity): Either<GetWalletSellPriceResponse, ApiResponseError> {
        val request = GetWalletSellPriceRequest(quantity)
        return apiClient.send(ServicesGateMethods.GET_COIN_SELL_PRICE.toString(), request, GetWalletSellPriceResponse::class.java)
    }

    override fun getConfig(): Either<GetConfigResponse, ApiResponseError> {
        val request = GetConfigRequest()
        return apiClient.send(ServicesGateMethods.GET_CONFIG.toString(), request, GetConfigResponse::class.java)
    }

    override fun quickSearch(queryString: String, limit: Int?, entities: List<SearchableEntities>?): Either<QuickSearchResponse, ApiResponseError> {
        val request = QuickSearchRequest(queryString, limit, entities?.map { it.toString() })
        return apiClient.send(ServicesGateMethods.SEARCH_QUICK.toString(), request, QuickSearchResponse::class.java)
    }

    override fun extendedSearch(queryString: String,
                                profilesSearchRequest: ExtendedRequestSearchItem?,
                                communitiesSearchRequest: ExtendedRequestSearchItem?,
                                postsSearchRequest: ExtendedRequestSearchItem?): Either<ExtendedSearchResponse, ApiResponseError> {
        val request = ExtendedSearchRequest(queryString, ExtendedSearchRequestEntities(profilesSearchRequest, communitiesSearchRequest, postsSearchRequest))
        return apiClient.send(ServicesGateMethods.SEARCH_EXTENDED.toString(), request, ExtendedSearchResponse::class.java)
    }

    override fun shutDown() {
        apiClient.dropConnection()
    }
}