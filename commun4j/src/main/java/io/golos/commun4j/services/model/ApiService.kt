package io.golos.commun4j.services.model


import io.golos.commun4j.chain.actions.transaction.abi.TransactionAbi
import io.golos.commun4j.http.rpc.model.ApiResponseError
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.model.*
import io.golos.commun4j.model.FeedTimeFrame
import io.golos.commun4j.sharedmodel.*
import io.golos.commun4j.utils.StringSigner
import java.net.SocketTimeoutException

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

    fun getUserProfile(userId: CyberName?, userName: String?): Either<GetProfileResult, ApiResponseError>

    fun getIframelyEmbed(forLink: String): Either<IFramelyEmbedResult, ApiResponseError>

    fun getOEmdedEmbed(forLink: String): Either<OEmbedResult, ApiResponseError>

    fun getRegistrationState(phone: String? = null, identity: String? = null, email: String? = null): Either<UserRegistrationStateResult, ApiResponseError>

    /** method leads to sending sms code to user's [phone]. proper [testingPass] makes backend to omit this check
     *  @param captcha capthc string
     *  @param phone  of user for sending sms verification code
     *  @param testingPass pass to omit cpatcha and phone checks
     *  @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
     *  @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     * */
    fun firstUserRegistrationStep(captcha: String?, captchaType: String?, phone: String, testingPass: String? = null): Either<FirstRegistrationStepResult, ApiResponseError>

    fun firstUserRegistrationStepEmail(captcha: String?, captchaType: String?, email: String, testingPass: String? = null): Either<FirstRegistrationStepEmailResult, ApiResponseError>

    /** method used to verify [phone] by sent [code] through sms. Second step of registration
     *  @param code sms code sent to [phone]
     *  @param phone  of user
     *  @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
     *  @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     * */
    fun verifyPhoneForUserRegistration(phone: String, code: Int): Either<VerifyStepResult, ApiResponseError>

    fun verifyEmailForUserRegistration(email: String, code: String): Either<VerifyStepResult, ApiResponseError>

    /** method used to connect verified [user] name with [phone]. Third step of registration
     *  @param user name to associate with [phone]
     *  @param phone verified phone
     *  @param identity
     *  @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
     *  @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     * */
    fun setVerifiedUserName(user: String, phone: String? = null, identity: String? = null, email: String? = null): Either<SetUserNameStepResult, ApiResponseError>

    /** method used to finalize registration of user in cyberway blockchain. Final step of registration
     *  @param userName name of user
     *  @param owner public owner key of user
     *  @param active public active key of user
     *  @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
     *  @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     * */
    fun writeUserToBlockChain(phone: String?, identity: String?, email: String?, userId: String, userName: String, owner: String, active: String): Either<WriteToBlockChainStepResult, ApiResponseError>

    fun appendReferralParent(phone: String?, identity: String?, email: String?,
                             refferalId: String, userId: CyberName?
    ): Either<ResultOk, ApiResponseError>

    /** method used to resend sms code to user during phone verification
     *  @param forUser name of user
     *  @param phone phone of user to verify
     *  @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
     *  @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     * */
    fun resendSmsCode(forUser: String? = null, phone: String? = null): Either<ResendSmsResult, ApiResponseError>

    fun resendEmail(email: String): Either<ResendEmailResult, ApiResponseError>

    fun getReferralUsers(limit: Int? = null, offset: Int? = null): Either<GetReferralUsersResponse, ApiResponseError>

    /** method will block thread until [blockNum] would consumed by prism services
     * @param blockNum num of block to wait
     * @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     */
    fun waitForABlock(blockNum: Long): Either<ResultOk, ApiResponseError>

    /** method will block thread until [transactionId] would be consumed by prism services. Old transaction are not stored in services.
     * @param transactionId userId of transaction to wait
     * @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     */
    fun waitForTransaction(transactionId: String): Either<ResultOk, ApiResponseError>

    fun subscribeOnMobilePushNotifications(deviceId: String, appName: String, fcmToken: String): Either<ResultOk, ApiResponseError>

    fun unSubscribeOnNotifications(userId: String, deviceId: String, appName: String): Either<ResultOk, ApiResponseError>

    fun setPushSettings(disable: List<NotificationType>): Either<ResultOk, ApiResponseError>

    fun getPushSettings(): Either<PushSettingsResponse, ApiResponseError>

    fun getEvents(userProfile: String, appName: String, afterId: String?, limit: Int?, markAsViewed: Boolean?, freshOnly: Boolean?, types: List<EventType>): Either<EventsData, ApiResponseError>

    fun markEventsAsRead(ids: List<String>, appName: String): Either<ResultOk, ApiResponseError>


    fun getUnreadCount(profileId: String, appName: String): Either<FreshResult, ApiResponseError>


    fun getSubscribers(userId: CyberName?,
                       communityId: String?,
                       limit: Int?,
                       offset: Int?): Either<SubscribedUsersResponse, ApiResponseError>

    /**part of auth process. It consists of 3 steps:
     * 1. getting secret string using method [getAuthSecret]
     * 2. signing it with [StringSigner]
     * 3. sending result using [authWithSecret] method
     *  @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     * */
    fun getAuthSecret(): Either<AuthSecret, ApiResponseError>

    /**part of auth process. It consists of 3 steps:
     * 1. getting secret string using method [getAuthSecret]
     * 2. signing it with [StringSigner]
     * 3. sending result using [authWithSecret] method
     *  @param userName userid, userName, domain name, or whatever current version of services willing to accept
     *  @param secret secret string, obtained from [getAuthSecret] method
     *  @param signedSecret [secret] signed with [StringSigner]
     *  @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     * */
    fun authWithSecret(userName: String,
                       secret: String,
                       signedSecret: String): Either<AuthResult, ApiResponseError>

    /**disconnects from microservices, effectively unaithing
     * Method will result  throwing all pending socket requests.
     * */
    fun unAuth()

    fun logout(): Either<ResultOk, ApiResponseError>

    /**function tries to resolve canonical name from domain (..@golos for example) or username
     * @param username userName to resolve to
     * @throws IllegalArgumentException if name doesn't exist
     * */
    fun resolveCanonicalCyberName(username: String): Either<ResolvedProfile, ApiResponseError>

    fun getCommunitiesList(type: CommunitiesRequestType? = null, userId: CyberName? = null,
                           search: String? = null, offset: Int? = null, limit: Int? = null): Either<GetCommunitiesResponse, ApiResponseError>

    fun getCommunity(communityId: String?, communityAlias: String?): Either<GetCommunitiesItem, ApiResponseError>

    fun shutDown()

    fun getPost(userId: CyberName,
                communityId: String,
                permlink: String): Either<CyberDiscussion, ApiResponseError>

    fun getPosts(userId: CyberName? = null,
                 communityId: String? = null,
                 communityAlias: String? = null,
                 allowNsfw: Boolean? = null,
                 type: FeedType? = null,
                 sortBy: FeedSortByType? = null,
                 timeframe: FeedTimeFrame? = null,
                 limit: Int? = null,
                 offset: Int? = null,
                 allowedLanguages: List<String>?): Either<GetDiscussionsResult, ApiResponseError>

    fun getPostRaw(userId: CyberName, communityId: String, permlink: String): Either<CyberDiscussionRaw, ApiResponseError>

    fun getPostsRaw(userId: CyberName? = null,
                    communityId: String? = null,
                    communityAlias: String? = null,
                    allowNsfw: Boolean? = null,
                    type: FeedType? = null,
                    sortBy: FeedSortByType? = null,
                    timeframe: FeedTimeFrame? = null,
                    limit: Int? = null,
                    offset: Int? = null,
                    allowedLanguages: List<String>?): Either<GetDiscussionsResultRaw, ApiResponseError>

    fun getTokensInfo(list: List<String>): Either<GetTokensInfoResponse, ApiResponseError>

    fun getLeaders(communityId: String, limit: Int? = null, skip: Int? = null, query: String? = null): Either<LeadersResponse, ApiResponseError>

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

    fun onBoardingCommunitySubscriptions(userId: CyberName, communityIds: List<String>): Either<ResultOk, ApiResponseError>

    fun getComment(userId: CyberName, communityId: String, permlink: String): Either<CyberComment, ApiResponseError>

    fun getCommentRaw(userId: CyberName, communityId: String, permlink: String): Either<CyberCommentRaw, ApiResponseError>

    fun getComments(sortBy: CommentsSortBy? = null, offset: Int? = null, limit: Int? = null, type: CommentsSortType? = null,
                    userId: CyberName? = null, permlink: String? = null, communityId: String? = null,
                    communityAlias: String? = null, parentComment: ParentComment? = null, resolveNestedComments: Boolean? = null): Either<GetCommentsResponse, ApiResponseError>

    fun getCommentsRaw(sortBy: CommentsSortBy? = null, offset: Int? = null, limit: Int? = null, type: CommentsSortType? = null,
                       userId: CyberName? = null, permlink: String? = null, communityId: String? = null,
                       communityAlias: String? = null, parentComment: ParentComment? = null, resolveNestedComments: Boolean? = null): Either<GetCommentsResponseRaw, ApiResponseError>

    fun getNotifications(limit: Int? = null, beforeThan: String? = null, filter: List<GetNotificationsFilter>? = null): Either<GetNotificationsResponse, ApiResponseError>

    fun getNotificationsSkipUnrecognized(limit: Int? = null, beforeThan: String? = null, filter: List<GetNotificationsFilter>? = null): Either<GetNotificationsResponse, ApiResponseError>

    fun getNotificationsStatus(): Either<GetNotificationStatusResponse, ApiResponseError>

    fun markAllNotificationAsViewed(until: String): Either<ResultOk, ApiResponseError>

    fun getStateBulk(posts: List<UserAndPermlinkPair>): Either<GetStateBulkResponse, ApiResponseError>

    fun subscribeOnNotifications(): Either<ResultOk, ApiResponseError>

    fun unSubscribeFromNotifications(): Either<ResultOk, ApiResponseError>

    fun getBalance(userId: CyberName): Either<GetUserBalanceResponse, ApiResponseError>

    fun getTransferHistory(userId: CyberName, direction: TransferHistoryDirection? = null, transferType: TransferHistoryTransferType? = null,
                           symbol: CyberSymbolCode? = null, rewards: String? = null, limit: Int? = null, offset: Int? = null): Either<GetTransferHistoryResponse, ApiResponseError>

    fun getBuyPrice(pointSymbol: CyberSymbolCode, quantity: WalletQuantity): Either<GetWalletBuyPriceResponse, ApiResponseError>

    fun getSellPrice(quantity: WalletQuantity): Either<GetWalletSellPriceResponse, ApiResponseError>

    fun getConfig(): Either<GetConfigResponse, ApiResponseError>

    fun quickSearch(queryString: String, limit: Int?, entities: List<SearchableEntities>?): Either<QuickSearchResponse, ApiResponseError>

    fun extendedSearch(queryString: String,
                       profilesSearchRequest: ExtendedRequestSearchItem?,
                       communitiesSearchRequest: ExtendedRequestSearchItem?,
                       postsSearchRequest: ExtendedRequestSearchItem?): Either<ExtendedSearchResponse, ApiResponseError>

    fun setInfo(timezoneOffset: Int): Either<ResultOk, ApiResponseError>

    fun setFcmToken(token: String): Either<ResultOk, ApiResponseError>

    fun resetFcmToken(): Either<ResultOk, ApiResponseError>

    fun recordPostView(userId: CyberName, communityId: String, permlink: String, deviceId: String): Either<ResultOk, ApiResponseError>

    fun getDonations(posts: List<DonationPostModel>): Either<GetDonationResponse, ApiResponseError>
}

