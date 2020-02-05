package io.golos.commun4j.services.model


import io.golos.commun4j.chain.actions.transaction.abi.TransactionAbi
import io.golos.commun4j.http.rpc.model.ApiResponseError
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.model.CyberDiscussion
import io.golos.commun4j.model.CyberDiscussionRaw
import io.golos.commun4j.model.GetDiscussionsResult
import io.golos.commun4j.model.GetDiscussionsResultRaw
import io.golos.commun4j.sharedmodel.*

/**
 *
 * */

interface ServicesTransactionPushService {
    fun <T : Any> pushTransactionWithProvidedBandwidth(chainId: String,
                                                       transactionAbi: TransactionAbi,
                                                       signature: String,
                                                       traceType: Class<T>): Either<TransactionCommitted<T>, GolosEosError>
}

interface ApiService : ServicesTransactionPushService {

    fun getProfile(userId: String?, username: String?): Either<GetProfileResult, ApiResponseError>

    fun getIframelyEmbed(forLink: String): Either<IFramelyEmbedResult, ApiResponseError>

    fun getOEmdedEmbed(forLink: String): Either<OEmbedResult, ApiResponseError>

    fun getRegistrationStateOf(userId: String?, phone: String?): Either<UserRegistrationStateResult, ApiResponseError>

    fun firstUserRegistrationStep(captcha: String?, phone: String, testingPass: String?): Either<FirstRegistrationStepResult, ApiResponseError>

    fun verifyPhoneForUserRegistration(phone: String, code: Int): Either<VerifyStepResult, ApiResponseError>

    fun setVerifiedUserName(user: String, phone: String): Either<SetUserNameStepResult, ApiResponseError>

    fun writeUserToBlockchain(phone: String, userId: String, userName: String, owner: String, active: String): Either<WriteToBlockChainStepResult, ApiResponseError>

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


    fun getUnreadCount(profileId: String, appName: String): Either<FreshResult, ApiResponseError>


    fun getSubscribers(userId: CyberName?,
                       communityId: String?,
                       limit: Int?,
                       offset: Int?): Either<SubscribedUsersResponse, ApiResponseError>

    fun getAuthSecret(): Either<AuthSecret, ApiResponseError>

    fun authWithSecret(user: String,
                       secret: String,
                       signedSecret: String): Either<AuthResult, ApiResponseError>

    fun unAuth()

    fun resolveProfile(username: String): Either<ResolvedProfile, ApiResponseError>

    fun getCommunitiesList(type: String?, userId: String?, search: String?, offset: Int?, limit: Int?): Either<GetCommunitiesResponse, ApiResponseError>

    fun getCommunity(communityId: String): Either<GetCommunitiesItem, ApiResponseError>

    fun shutDown()

    fun getPost(userId: CyberName,
                communityId: String,
                permlink: String): Either<CyberDiscussion, ApiResponseError>

    fun getPosts(userId: String?,
                 communityId: String?,
                 communityAlias: String?,
                 allowNsfw: Boolean?,
                 type: String?,
                 sortBy: String?,
                 timeframe: String?,
                 limit: Int?,
                 offset: Int?): Either<GetDiscussionsResult, ApiResponseError>

    fun getPostRaw(userId: CyberName, communityId: String, permlink: String): Either<CyberDiscussionRaw, ApiResponseError>

    fun getPostsRaw(userId: String?,
                    communityId: String?,
                    communityAlias: String?,
                    allowNsfw: Boolean?,
                    type: String?,
                    sortBy: String?,
                    timeframe: String?,
                    limit: Int?,
                    offset: Int?): Either<GetDiscussionsResultRaw, ApiResponseError>

    fun getTokensInfo(list: List<String>): Either<GetTokensInfoResponse, ApiResponseError>

    fun getLeaders(communityId: String, limit: Int?, skip: Int?, query: String?): Either<LeadersResponse, ApiResponseError>

    fun getCommunityBlacklist(communityId: String?,        // Id сообщества
                              communityAlias: String?,// Алиас сообщества
                              offset: Int?,              // Сдвиг пагинации
                              limit: Int?): Either<CommunityBlacklistResponse, ApiResponseError>

    fun getBlacklistedUsers(userId: CyberName): Either<BlacklistedUsersResponse, ApiResponseError>

    fun getBlacklistedCommunities(userId: CyberName): Either<BlacklistedCommunitiesResponse, ApiResponseError>

    fun getUserSubscriptions(ofUser: CyberName,
                             limit: Int?,
                             offset: Int?): Either<UserSubscriptionsResponse, ApiResponseError>

    fun getCommunitySubscriptions(ofUser: CyberName,
                                  limit: Int?,
                                  offset: Int?): Either<CommunitySubscriptionsResponse, ApiResponseError>

    fun getReports(communityIds: List<String>?,
                   status: ReportsRequestStatus?,
                   contentType: ReportRequestContentType?,
                   sortBy: ReportsRequestTimeSort?,
                   limit: Int?,
                   offset: Int?): Either<GetReportsResponse, ApiResponseError>

    fun getReportsRaw(communityIds: List<String>?,
                      status: ReportsRequestStatus?,
                      contentType: ReportRequestContentType?,
                      sortBy: ReportsRequestTimeSort?,
                      limit: Int?,
                      offset: Int?): Either<GetReportsResponseRaw, ApiResponseError>

    fun suggestNames(text: String): Either<SuggestNameResponse, ApiResponseError>

    fun onBoardingCommunitySubscriptions(name: String, communityIds: List<String>): Either<ResultOk, ApiResponseError>

    fun getComment(name: String, communityId: String, permlink: String): Either<CyberComment, ApiResponseError>

    fun getCommentRaw(name: String, communityId: String, permlink: String): Either<CyberCommentRaw, ApiResponseError>

    fun getComments(sortBy: String?, offset: Int?, limit: Int?, type: String?, userId: String?, permlink: String?, communityId: String?, communityAlias: String?, parentComment: ParentComment?, resolveNestedComments: Boolean?): Either<GetCommentsResponse, ApiResponseError>

    fun getCommentsRaw(sortBy: String?, offset: Int?, limit: Int?, type: String?, userId: String?, permlink: String?, communityId: String?, communityAlias: String?, parentComment: ParentComment?, resolveNestedComments: Boolean?): Either<GetCommentsResponseRaw, ApiResponseError>

    fun getNotifications(limit: Int?, beforeThan: String?, filter: List<GetNotificationsFilter>?): Either<GetNotificationsResponse, ApiResponseError>

    fun getNotificationsSafe(limit: Int?, beforeThan: String?, filter: List<GetNotificationsFilter>?): Either<GetNotificationsResponse, ApiResponseError>

    fun getNotificationsStatus(): Either<GetNotificationStatusResponse, ApiResponseError>

    fun markAllNotificationAsViewed(until: String): Either<ResultOk, ApiResponseError>

    fun getStateBulk(posts: List<UserAndPermlinkPair>): Either<GetStateBulkResponse, ApiResponseError>

    fun subscribeOnNotifications(): Either<ResultOk, ApiResponseError>

    fun unSubscribeFromNotifications(): Either<ResultOk, ApiResponseError>

    fun getWalletBalance(userId: CyberName): Either<GetUserBalanceResponse, ApiResponseError>

    fun getTransferHistory(userId: CyberName, direction: TransferHistoryDirection?, transferType: TransferHistoryTransferType?,
                           symbol: CyberSymbolCode?, rewards: String?, limit: Int?, offset: Int?): Either<GetTransferHistoryResponse, ApiResponseError>

    fun getBuyPrice(pointSymbol: CyberSymbolCode, quantity: WalletQuantity): Either<GetWalletBuyPriceResponse, ApiResponseError>

    fun getSellPrice(quantity: WalletQuantity): Either<GetWalletSellPriceResponse, ApiResponseError>
}

