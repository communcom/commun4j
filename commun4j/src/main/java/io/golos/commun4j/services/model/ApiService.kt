package io.golos.commun4j.services.model


import io.golos.commun4j.chain.actions.transaction.abi.TransactionAbi
import io.golos.commun4j.http.rpc.model.ApiResponseError
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.model.CyberDiscussion
import io.golos.commun4j.model.DiscussionsResult
import io.golos.commun4j.sharedmodel.AuthSecret
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.Either
import io.golos.commun4j.sharedmodel.GolosEosError

/** listener interface for auth state in cyber microservices.
 *
 * */

interface ApiService {

    fun getDiscussions(feedType: PostsFeedType,
                       sort: FeedSort,
                       timeFrame: FeedTimeFrame?,
                       parsingType: ContentParsingType,
                       sequenceKey: String?,
                       limit: Int,
                       userId: String?,
                       communityId: String?,
                       tags: List<String>?,
                       username: String?,
                       app: String): Either<DiscussionsResult, ApiResponseError>

    fun getPost(userId: String?,
                username: String?,
                permlink: String,
                parsingType: ContentParsingType,
                appName: String): Either<CyberDiscussion, ApiResponseError>

    fun getComment(userId: String?,
                   permlink: String,
                   parsingType: ContentParsingType,
                   username: String?,
                   app: String): Either<CyberDiscussion, ApiResponseError>

    fun getComments(sort: FeedSort?,
                    sequenceKey: String?,
                    limit: Int?,
                    origin: CommentsOrigin?,
                    parsingType: ContentParsingType,
                    userId: String?,
                    permlink: String?,
                    username: String?,
                    appName: String): Either<DiscussionsResult, ApiResponseError>


    fun getProfile(userId: String?, username: String?): Either<GetProfileResult, ApiResponseError>

    fun getIframelyEmbed(forLink: String): Either<IFramelyEmbedResult, ApiResponseError>

    fun getOEmdedEmbed(forLink: String): Either<OEmbedResult, ApiResponseError>

    fun getRegistrationStateOf(userId: String?, phone: String?): Either<UserRegistrationStateResult, ApiResponseError>

    fun firstUserRegistrationStep(captcha: String?, phone: String, testingPass: String?): Either<FirstRegistrationStepResult, ApiResponseError>

    fun verifyPhoneForUserRegistration(phone: String, code: Int): Either<ResultOk, ApiResponseError>

    fun setVerifiedUserName(user: String, phone: String): Either<ResultOk, ApiResponseError>

    fun writeUserToBlockchain(userName: String, owner: String, active: String, posting: String, memo: String): Either<RegisterResult, ApiResponseError>

    fun resendSmsCode(name: String?, phone: String?): Either<ResultOk, ApiResponseError>

    fun waitBlock(blockNum: Long): Either<ResultOk, ApiResponseError>

    fun waitForTransaction(transactionId: String): Either<ResultOk, ApiResponseError>

    fun subscribeOnMobilePushNotifications(deviceId: String, appName: String, fcmToken: String): Either<ResultOk, ApiResponseError>

    fun unSubscribeOnNotifications(userId: String, deviceId: String, appName: String): Either<ResultOk, ApiResponseError>

    fun setNotificationSettings(deviceId: String, app: String,
                                newBasicSettings: Any?, newWebNotifySettings: WebShowSettings?, newMobilePushSettings: MobileShowSettings?): Either<ResultOk, ApiResponseError>

    fun getNotificationSettings(deviceId: String, app: String): Either<UserSettings, ApiResponseError>

    fun getEvents(userProfile: String, appName: String, afterId: String?, limit: Int?, markAsViewed: Boolean?, freshOnly: Boolean?, types: List<EventType>): Either<EventsData, ApiResponseError>

    fun markEventsAsRead(ids: List<String>, appName: String): Either<ResultOk, ApiResponseError>

    fun markAllEventsAsRead(appName: String): Either<ResultOk, ApiResponseError>

    fun getUnreadCount(profileId: String, appName: String): Either<FreshResult, ApiResponseError>

    fun getSubscriptions(ofUser: CyberName, limit: Int, type: SubscriptionType, sequenceKey: String?, appName: String): Either<SubscriptionsResponse, ApiResponseError>

    fun getSubscribers(ofUser: CyberName, limit: Int, type: SubscriptionType, sequenceKey: String?, appName: String): Either<SubscribersResponse, ApiResponseError>

    fun getAuthSecret(): Either<AuthSecret, ApiResponseError>

    fun authWithSecret(user: String,
                       secret: String,
                       signedSecret: String): Either<AuthResult, ApiResponseError>

    fun unAuth()

    fun resolveProfile(username: String,
                       appName: String): Either<ResolvedProfile, ApiResponseError>

    fun <T : Any> pushTransactionWithProvidedBandwidth(chainId: String,
                                                       transactionAbi: TransactionAbi,
                                                       signature: String,
                                                       traceType: Class<T>): Either<TransactionCommitted<T>, GolosEosError>

    fun getCommunitiesList(name: String, offset: Int): Either<GetCommunitiesResponse, ApiResponseError>

    fun getCommunity(communityId: String, userId: String): Either<GetCommunitiesItem, ApiResponseError>

    fun shutDown()


}

