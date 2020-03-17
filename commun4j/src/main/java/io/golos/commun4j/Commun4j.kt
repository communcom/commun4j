@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.golos.commun4j

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import io.golos.commun4j.abi.implementation.IAction
import io.golos.commun4j.abi.implementation.c.ctrl.UnvoteleadCCtrlAction
import io.golos.commun4j.abi.implementation.c.ctrl.UnvoteleadCCtrlStruct
import io.golos.commun4j.abi.implementation.c.ctrl.VoteleaderCCtrlAction
import io.golos.commun4j.abi.implementation.c.ctrl.VoteleaderCCtrlStruct
import io.golos.commun4j.abi.implementation.c.gallery.*
import io.golos.commun4j.abi.implementation.c.list.*
import io.golos.commun4j.abi.implementation.c.point.TransferArgsCPointStruct
import io.golos.commun4j.abi.implementation.c.point.TransferCPointAction
import io.golos.commun4j.abi.implementation.c.point.TransferCPointStruct
import io.golos.commun4j.abi.implementation.c.social.*
import io.golos.commun4j.abi.implementation.cyber.domain.NewusernameCyberDomainAction
import io.golos.commun4j.abi.implementation.cyber.domain.NewusernameCyberDomainStruct
import io.golos.commun4j.abi.implementation.cyber.token.OpenCyberTokenAction
import io.golos.commun4j.abi.implementation.cyber.token.OpenCyberTokenStruct
import io.golos.commun4j.abi.implementation.cyber.token.TransferCyberTokenAction
import io.golos.commun4j.abi.implementation.cyber.token.TransferCyberTokenStruct
import io.golos.commun4j.chain.actions.transaction.TransactionPusher
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.core.crypto.EosPrivateKey
import io.golos.commun4j.http.rpc.RpcServerMessage
import io.golos.commun4j.http.rpc.RpcServerMessageCallback
import io.golos.commun4j.http.rpc.model.ApiResponseError
import io.golos.commun4j.http.rpc.model.account.request.AccountName
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.model.*
import io.golos.commun4j.model.FeedTimeFrame
import io.golos.commun4j.services.CyberServicesApiService
import io.golos.commun4j.services.model.*
import io.golos.commun4j.sharedmodel.*
import io.golos.commun4j.utils.StringSigner
import java.io.File
import java.net.SocketTimeoutException
import java.util.*
import java.util.concurrent.Callable

open class Commun4j @JvmOverloads constructor(
        val config: Commun4jConfig,

        chainApiProvider: ChainApiProvider? = null,
        val keyStorage: KeyStorage = KeyStorage(),
        private val serverMessageCallback: RpcServerMessageCallback = object : RpcServerMessageCallback {
            override fun onMessage(message: RpcServerMessage) {

            }
        },
        private val apiService: ApiService = CyberServicesApiService(config, serverMessageCallback = serverMessageCallback)) {

    private val transactionPusher: ITransactionPusherBridge = TransactionPusherBridge(config, TransactionPusher, ServicesTransactionPusher(config, apiService, TransactionPusher))
    private val chainApi: CyberWayChainApi
    private val moshi: Moshi = Moshi
            .Builder()
            .add(CyberName::class.java, CyberNameAdapter())
            .add(CyberAsset::class.java, CyberAssetAdapter())
            .add(CyberSymbol::class.java, CyberSymbolAdapter())
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .add(CyberTimeStampSeconds::class.java, CyberTimeStampAdapter())
            .add(Varuint::class.java, VariableUintAdapter())
            .add(CyberTimeStampMsAdapter::class.java, CyberTimeStampMsAdapter())
            .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
            .build()

    private val actionsResolver = ::resolveActions
    private val transactionRecovers: List<RecoverFunction> = listOf(::recoverFromBalanceDoesNotExistError,
            ::recoverFromStaleError)

    init {
        chainApi = chainApiProvider?.provide() ?: GolosEosConfiguratedApi(config, moshi).provide()
    }

    internal fun createPermlink(parentPermlink: String?): String {
        val timeStamp = (Date().time / 1000).toString()
        return when {
            parentPermlink == null -> timeStamp //permlink of post
            parentPermlink.trim().startsWith("re-re") -> parentPermlink.trim().replace(Regex("-[0-9]+\$"), "-$timeStamp")//permlink to comment to coment
            parentPermlink.trim().startsWith("re-") -> "re-${parentPermlink.trim().replace(Regex("-[0-9]+\$"), "-$timeStamp")}"  //permlink to a coment
            else -> "re-${parentPermlink.trim()}-$timeStamp"
        }
    }


    /**function tries to resolve canonical name from domain (..@golos for example) or username
     * @param name userName to resolve to
     * @throws IllegalArgumentException if name doesn't exist
     * */
    fun resolveCanonicalCyberName(name: String) = apiService.resolveProfile(name)


    private inline fun <reified T : Any> pushTransaction(
            originalAction: IAction,
            actions: List<ActionAbi>,
            actorKey: EosPrivateKey,
            bandWidthRequest: BandWidthRequest?,
            clientAuthRequest: ClientAuthRequest?): Either<TransactionCommitted<T>, GolosEosError> {

        val keys = actorKey
                .asList()
                .plus(clientAuthRequest?.keys.orEmpty())

        val callable = Callable {
            transactionPusher.pushTransaction(
                    actionsResolver(actions, bandWidthRequest, clientAuthRequest),
                    actorKey
                            .asList()
                            .plus(clientAuthRequest?.keys.orEmpty()),
                    T::class.java,
                    bandWidthRequest
            )
        }
        return callWithRecover(callable, transactionRecovers, originalAction, keys, transactionPusher, bandWidthRequest, clientAuthRequest)
    }


    private inline fun <reified T : Any> pushTransaction(
            originalAction: IAction,
            auth: TransactionAuthorizationAbi,
            key: String,
            bandWidthRequest: BandWidthRequest?,
            clientAuthRequest: ClientAuthRequest?): Either<TransactionCommitted<T>, GolosEosError> {

        return pushTransaction(originalAction,
                listOf(originalAction.asActionAbi(listOf(auth))),
                EosPrivateKey(key),
                bandWidthRequest,
                clientAuthRequest)
    }

    /**method sets  nickname for active user. Method assumes, that there is some active user in [keyStorage]
     * cyber.domain@newusername action
     * Nick must match [0-9a-z.-]{1,32}
     * @param owner owner of domain
     * @param newUserName new nickName for user
     * @param creatorKey active key of [owner]
     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     * */
    @JvmOverloads
    fun newUserName(owner: CyberName,
                    newUserName: String,
                    creatorKey: String,
                    forUser: CyberName = keyStorage.getActiveAccount(),
                    bandWidthRequest: BandWidthRequest? = null): Either<TransactionCommitted<NewusernameCyberDomainStruct>, GolosEosError> {

        if (!newUserName.matches("[0-9a-z.-]{1,32}".toRegex())) throw java.lang.IllegalArgumentException("nick must match [0-9a-z.-]{1,32}")

        return pushTransaction(NewusernameCyberDomainAction(
                NewusernameCyberDomainStruct(owner, forUser, newUserName)),
                TransactionAuthorizationAbi(owner.name, "active"),
                creatorKey,
                bandWidthRequest,
                null)
    }

    /** c.gallery@create action
     * */
    @JvmOverloads
    fun createPost(
            communCode: CyberSymbolCode,
            header: String,
            body: String,
            tags: List<String>,
            metadata: String,
            weight: Short?,
            bandWidthRequest: BandWidthRequest? = null,
            clientAuthRequest: ClientAuthRequest? = null,
            author: CyberName = keyStorage.getActiveAccount(),
            authorKey: String = keyStorage.getActiveKeyOfActiveAccount()): Either<TransactionCommitted<CreateCGalleryStruct>, GolosEosError> {


        return pushTransaction(CreateCGalleryAction(
                CreateCGalleryStruct(communCode,
                        MssgidCGalleryStruct(author, createPermlink(null)),
                        MssgidCGalleryStruct(CyberName(), ""),
                        header,
                        body,
                        tags,
                        metadata,
                        weight)),
                TransactionAuthorizationAbi(author.name, "active"),
                authorKey,
                bandWidthRequest,
                clientAuthRequest)
    }

    /** c.gallery@create action
     * */
    @JvmOverloads
    fun createComment(
            parentMssgId: MssgidCGalleryStruct,
            communCode: CyberSymbolCode,
            header: String,
            body: String,
            tags: List<String>,
            metadata: String,
            weight: Short?,
            bandWidthRequest: BandWidthRequest? = null,
            clientAuthRequest: ClientAuthRequest? = null,
            author: CyberName = keyStorage.getActiveAccount(),
            authorKey: String = keyStorage.getActiveKeyOfActiveAccount()): Either<TransactionCommitted<CreateCGalleryStruct>, GolosEosError> {


        return pushTransaction(CreateCGalleryAction(
                CreateCGalleryStruct(communCode,
                        MssgidCGalleryStruct(author, createPermlink(parentMssgId.permlink)),
                        parentMssgId,
                        header,
                        body,
                        tags,
                        metadata,
                        weight)),
                TransactionAuthorizationAbi(author.name, "active"),
                authorKey,
                bandWidthRequest,
                clientAuthRequest)
    }

    /** c.gallery@update action
     * */
    @JvmOverloads
    fun updatePostOrComment(
            messageId: MssgidCGalleryStruct,
            communCode: CyberSymbolCode,
            header: String,
            body: String,
            tags: List<String>,
            metadata: String,
            bandWidthRequest: BandWidthRequest? = null,
            clientAuthRequest: ClientAuthRequest? = null,
            author: CyberName = keyStorage.getActiveAccount(),
            authorKey: String = keyStorage.getActiveKeyOfActiveAccount()): Either<TransactionCommitted<UpdateCGalleryStruct>, GolosEosError> {


        return pushTransaction<UpdateCGalleryStruct>(UpdateCGalleryAction(
                UpdateCGalleryStruct(communCode,
                        messageId,
                        header,
                        body,
                        tags,
                        metadata)),
                TransactionAuthorizationAbi(author.name, "active"), authorKey,
                bandWidthRequest, clientAuthRequest)
    }

    /** c.gallery@remove action
     * */
    @JvmOverloads
    fun deletePostOrComment(
            messageId: MssgidCGalleryStruct,
            communCode: CyberSymbolCode,
            bandWidthRequest: BandWidthRequest? = null,
            clientAuthRequest: ClientAuthRequest? = null,
            author: CyberName = keyStorage.getActiveAccount(),
            authorKey: String = keyStorage.getActiveKeyOfActiveAccount()): Either<TransactionCommitted<RemoveCGalleryStruct>, GolosEosError> {

        return pushTransaction<RemoveCGalleryStruct>(RemoveCGalleryAction(
                RemoveCGalleryStruct(communCode,
                        messageId)
        ), TransactionAuthorizationAbi(author.name, "active"), authorKey, bandWidthRequest, clientAuthRequest)

    }

    /** c.gallery@upvote action
     * */
    @JvmOverloads
    fun upVote(communCode: CyberSymbolCode,
               messageId: MssgidCGalleryStruct,
               weight: Short,
               bandWidthRequest: BandWidthRequest? = null,
               clientAuthRequest: ClientAuthRequest? = null,
               voter: CyberName = keyStorage.getActiveAccount(),
               key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<UpvoteCGalleryStruct>, GolosEosError> {


        return pushTransaction(UpvoteCGalleryAction(UpvoteCGalleryStruct(communCode,
                voter, messageId, weight)
        ), TransactionAuthorizationAbi(voter.name, "active"), key, bandWidthRequest, clientAuthRequest)

    }

    /** c.gallery@downvote action
     * */
    @JvmOverloads
    fun downVote(communCode: CyberSymbolCode,
                 messageId: MssgidCGalleryStruct,
                 weight: Short,
                 bandWidthRequest: BandWidthRequest? = null,
                 clientAuthRequest: ClientAuthRequest? = null,
                 voter: CyberName = keyStorage.getActiveAccount(),
                 key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<DownvoteCGalleryStruct>, GolosEosError> {


        return pushTransaction(DownvoteCGalleryAction(DownvoteCGalleryStruct(communCode,
                voter, messageId, weight)
        ), TransactionAuthorizationAbi(voter.name, "active"), key, bandWidthRequest, clientAuthRequest)
    }

    /** c.gallery@unvote action
     * */
    @JvmOverloads
    fun unVote(communCode: CyberSymbolCode,
               messageId: MssgidCGalleryStruct,
               bandWidthRequest: BandWidthRequest? = null,
               clientAuthRequest: ClientAuthRequest? = null,
               voter: CyberName = keyStorage.getActiveAccount(),
               key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<UnvoteCGalleryStruct>, GolosEosError> {


        return pushTransaction<UnvoteCGalleryStruct>(UnvoteCGalleryAction(UnvoteCGalleryStruct(
                communCode, voter, messageId
        )), TransactionAuthorizationAbi(voter.name, "active"), key, bandWidthRequest, clientAuthRequest)
    }

    /** c.social@pin action
     * */
    @JvmOverloads
    fun pinUser(pinning: CyberName,
                bandWidthRequest: BandWidthRequest? = null,
                clientAuthRequest: ClientAuthRequest? = null,
                pinner: CyberName = keyStorage.getActiveAccount(),
                key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<PinCSocialStruct>, GolosEosError> {
        return pushTransaction<PinCSocialStruct>(PinCSocialAction(PinCSocialStruct(
                pinner, pinning
        )), TransactionAuthorizationAbi(pinner.name, "active"), key, bandWidthRequest, clientAuthRequest)

    }

    /** c.social@unpin action
     * */
    @JvmOverloads
    fun unpinUser(unpinning: CyberName,
                  bandWidthRequest: BandWidthRequest? = null,
                  clientAuthRequest: ClientAuthRequest? = null,
                  pinner: CyberName = keyStorage.getActiveAccount(),
                  key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<UnpinCSocialStruct>, GolosEosError> {


        return pushTransaction(UnpinCSocialAction(UnpinCSocialStruct(
                pinner, unpinning
        )), TransactionAuthorizationAbi(pinner.name, "active"), key, bandWidthRequest, clientAuthRequest)

    }

    /** c.listl@follow action
     * */
    @JvmOverloads
    fun followCommunity(communityCode: CyberSymbolCode,
                        bandWidthRequest: BandWidthRequest? = null,
                        clientAuthRequest: ClientAuthRequest? = null,
                        follower: CyberName = keyStorage.getActiveAccount(),
                        key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<FollowCListStruct>, GolosEosError> {


        return pushTransaction<FollowCListStruct>(FollowCListAction(FollowCListStruct(
                communityCode, follower
        )), TransactionAuthorizationAbi(follower.name, "active"), key, bandWidthRequest, clientAuthRequest)


    }

    /** c.listl@unfollow action
     * */
    @JvmOverloads
    fun unFollowCommunity(communityCode: CyberSymbolCode,
                          bandWidthRequest: BandWidthRequest? = null,
                          clientAuthRequest: ClientAuthRequest? = null,
                          follower: CyberName = keyStorage.getActiveAccount(),
                          key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<UnfollowCListStruct>, GolosEosError> {

        return pushTransaction(UnfollowCListAction(UnfollowCListStruct(
                communityCode, follower
        )), TransactionAuthorizationAbi(follower.name, "active"), key, bandWidthRequest, clientAuthRequest)
    }

    /** c.sociall@block action
     * */
    @JvmOverloads
    fun block(blocking: CyberName,
              bandWidthRequest: BandWidthRequest? = null,
              clientAuthRequest: ClientAuthRequest? = null,
              blocker: CyberName = keyStorage.getActiveAccount(),
              key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<BlockCSocialStruct>, GolosEosError> {
        return pushTransaction<BlockCSocialStruct>(BlockCSocialAction(BlockCSocialStruct(
                blocker, blocking
        )), TransactionAuthorizationAbi(blocker.name, "active"), key, bandWidthRequest, clientAuthRequest)
    }

    /** c.sociall@unblock action
     * */
    @JvmOverloads
    fun unBlock(blocking: CyberName,
                bandWidthRequest: BandWidthRequest? = null,
                clientAuthRequest: ClientAuthRequest? = null,
                blocker: CyberName = keyStorage.getActiveAccount(),
                key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<UnblockCSocialStruct>, GolosEosError> {
        return pushTransaction(UnblockCSocialAction(UnblockCSocialStruct(
                blocker, blocking
        )), TransactionAuthorizationAbi(blocker.name, "active"), key, bandWidthRequest, clientAuthRequest)
    }

    /** c.list@ban action
     * */
    @JvmOverloads
    fun banUserFromCommunity(communCode: CyberSymbolCode,
                             reason: String,
                             account: CyberName,
                             actor: CyberName = keyStorage.getActiveAccount(),
                             bandWidthRequest: BandWidthRequest? = null,
                             clientAuthRequest: ClientAuthRequest? = null,
                             key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<BanCListStruct>, GolosEosError> {
        return pushTransaction<BanCListStruct>(BanCListAction(BanCListStruct(
                communCode, account, reason
        )), TransactionAuthorizationAbi(actor.name, "active"), key, bandWidthRequest, clientAuthRequest)
    }

    /** c.list@unban action
     * */
    @JvmOverloads
    fun unBanUserFromCommunity(communCode: CyberSymbolCode,
                               reason: String,
                               account: CyberName,
                               actor: CyberName = keyStorage.getActiveAccount(),
                               bandWidthRequest: BandWidthRequest? = null,
                               clientAuthRequest: ClientAuthRequest? = null,
                               key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<UnbanCListStruct>, GolosEosError> {
        return pushTransaction(UnbanCListAction(UnbanCListStruct(
                communCode, account, reason
        )), TransactionAuthorizationAbi(actor.name, "active"), key, bandWidthRequest, clientAuthRequest)
    }

    /** c.list@hide action
     * */
    @JvmOverloads
    fun hide(communCode: CyberSymbolCode,
             user: CyberName = keyStorage.getActiveAccount(),
             bandWidthRequest: BandWidthRequest? = null,
             clientAuthRequest: ClientAuthRequest? = null,
             key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<HideCListStruct>, GolosEosError> {


        return pushTransaction(HideCListAction(HideCListStruct(
                communCode, user
        )), TransactionAuthorizationAbi(user.name, "active"), key, bandWidthRequest, clientAuthRequest)
    }

    /** c.list@unhide action
     * */
    @JvmOverloads
    fun unHide(communCode: CyberSymbolCode,
               user: CyberName = keyStorage.getActiveAccount(),
               bandWidthRequest: BandWidthRequest? = null,
               clientAuthRequest: ClientAuthRequest? = null,
               key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<UnhideCListStruct>, GolosEosError> {
        return pushTransaction(UnhideCListAction(UnhideCListStruct(
                communCode, user
        )), TransactionAuthorizationAbi(user.name, "active"), key, bandWidthRequest, clientAuthRequest)
    }

    /** c.social@updatemeta action
     * */
    @JvmOverloads
    fun updateUserMetadata(
            avatarUrl: String?,
            coverUrl: String?,
            biography: String?,
            facebook: String?,
            telegram: String?,
            whatsapp: String?,
            wechat: String?,
            firstName: String?,
            lastName: String?,
            country: String?,
            city: String?,
            birth_date: String?,
            instagram: String?,
            linkedin: String?,
            twitter: String?,
            github: String?,
            websiteUrl: String?,
            bandWidthRequest: BandWidthRequest? = null,
            clientAuthRequest: ClientAuthRequest? = null,
            user: CyberName = keyStorage.getActiveAccount(),
            key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<UpdatemetaCSocialStruct>, GolosEosError> {
        return pushTransaction<UpdatemetaCSocialStruct>(UpdatemetaCSocialAction(UpdatemetaCSocialStruct(
                user,
                AccountmetaCSocialStruct(
                        avatarUrl, coverUrl, biography, facebook, telegram, whatsapp, wechat, firstName,
                        lastName, country, city, birth_date, instagram, linkedin, twitter, github, websiteUrl
                )
        )), TransactionAuthorizationAbi(user.name, "active"), key, bandWidthRequest, clientAuthRequest)
    }

    /** c.social@deletemeta action
     * */
    @JvmOverloads
    fun deleteUserMetadata(
            bandWidthRequest: BandWidthRequest? = null,
            clientAuthRequest: ClientAuthRequest? = null,
            user: CyberName = keyStorage.getActiveAccount(),
            key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<DeletemetaCSocialStruct>, GolosEosError> {
        return pushTransaction<DeletemetaCSocialStruct>(DeletemetaCSocialAction(DeletemetaCSocialStruct(
                user
        )), TransactionAuthorizationAbi(user.name, "active"), key, bandWidthRequest, clientAuthRequest)
    }

    /** c.gallery@report action
     * */
    @JvmOverloads
    fun reportContent(
            communityCode: CyberSymbolCode,
            messageId: MssgidCGalleryStruct,
            reason: String,
            bandWidthRequest: BandWidthRequest? = null,
            clientAuthRequest: ClientAuthRequest? = null,
            reporter: CyberName = keyStorage.getActiveAccount(),
            key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<ReportCGalleryStruct>, GolosEosError> {
        return pushTransaction<ReportCGalleryStruct>(ReportCGalleryAction(ReportCGalleryStruct(
                communityCode, reporter, messageId, reason
        )), TransactionAuthorizationAbi(reporter.name, "active"), key, bandWidthRequest, clientAuthRequest)
    }

    /** c.ctrl@voteleader action
     * */
    @JvmOverloads
    fun voteLeader(communCode: CyberSymbolCode,
                   leader: CyberName,
                   pct: Short?,
                   bandWidthRequest: BandWidthRequest? = null,
                   voter: CyberName = keyStorage.getActiveAccount(),
                   key: String = keyStorage.getActiveKeyOfActiveAccount()): Either<TransactionCommitted<VoteleaderCCtrlStruct>, GolosEosError> {
        return pushTransaction<VoteleaderCCtrlStruct>(VoteleaderCCtrlAction(VoteleaderCCtrlStruct(
                communCode, voter, leader, pct
        )), TransactionAuthorizationAbi(voter.name, "active"), key, bandWidthRequest, null)
    }

    /** c.ctrl@unvotelead action
     * */
    @JvmOverloads
    fun unVoteLeader(communCode: CyberSymbolCode,
                     leader: CyberName,
                     bandWidthRequest: BandWidthRequest? = null,
                     voter: CyberName = keyStorage.getActiveAccount(),
                     key: String = keyStorage.getActiveKeyOfActiveAccount()): Either<TransactionCommitted<VoteleaderCCtrlStruct>, GolosEosError> {
        return pushTransaction<VoteleaderCCtrlStruct>(UnvoteleadCCtrlAction(UnvoteleadCCtrlStruct(
                communCode, voter, leader
        )), TransactionAuthorizationAbi(voter.name, "active"), key, bandWidthRequest, null)
    }


    @JvmOverloads
    fun getCommunitiesList(type: CommunitiesRequestType? = null, userId: CyberName? = null,
                           search: String? = null, offset: Int? = null, limit: Int? = null) = apiService.getCommunitiesList(type?.toString(), userId?.name, search, offset, limit)

    fun getCommunity(communityId: String?, communityAlias: String?) = apiService.getCommunity(communityId, communityAlias)


    fun getPost(
            userId: CyberName,
            communityId: String,
            permlink: String
    ) = apiService.getPost(userId, communityId, permlink)

    fun getPostRaw(
            userId: CyberName,
            communityId: String,
            permlink: String
    ) = apiService.getPostRaw(userId, communityId, permlink)

    fun getPosts(userId: CyberName? = null,
                 communityId: String? = null,
                 communityAlias: String? = null,
                 allowNsfw: Boolean? = null,
                 type: FeedType? = null,
                 sortBy: FeedSortByType? = null,
                 timeframe: FeedTimeFrame? = null,
                 limit: Int? = null,
                 offset: Int? = null) = apiService.getPosts(userId?.name,
            communityId, communityAlias,
            allowNsfw, type?.toString(),
            sortBy?.toString(),
            timeframe?.toString(), limit, offset)

    fun getPostsRaw(userId: CyberName? = null,
                    communityId: String? = null,
                    communityAlias: String? = null,
                    allowNsfw: Boolean? = null,
                    type: FeedType? = null,
                    sortBy: FeedSortByType? = null,
                    timeframe: FeedTimeFrame? = null,
                    limit: Int? = null,
                    offset: Int? = null) = apiService.getPostsRaw(userId?.name,
            communityId, communityAlias,
            allowNsfw, type?.toString(),
            sortBy?.toString(),
            timeframe?.toString(), limit, offset)

    fun getIframelyEmbed(forLink: String): Either<IFramelyEmbedResult, ApiResponseError> = apiService.getIframelyEmbed(forLink)

    fun getOEmdedEmbed(forLink: String): Either<OEmbedResult, ApiResponseError> = apiService.getOEmdedEmbed(forLink)

    fun getComment(userId: CyberName, communityId: String, permlink: String): Either<CyberComment, ApiResponseError> =
            apiService.getComment(userId.name, communityId, permlink)

    fun getCommentRaw(userId: CyberName, communityId: String, permlink: String): Either<CyberCommentRaw, ApiResponseError> =
            apiService.getCommentRaw(userId.name, communityId, permlink)

    @JvmOverloads
    fun getComments(sortBy: CommentsSortBy? = null, offset: Int? = null, limit: Int? = null, type: CommentsSortType? = null,
                    userId: CyberName? = null, permlink: String? = null, communityId: String? = null,
                    communityAlias: String? = null, parentComment: ParentComment? = null, resolveNestedComments: Boolean? = null): Either<GetCommentsResponse, ApiResponseError> =
            apiService.getComments(sortBy?.toString(), offset, limit, type?.toString(), userId?.name, permlink,
                    communityId, communityAlias, parentComment, resolveNestedComments)

    @JvmOverloads
    fun getCommentsRaw(sortBy: CommentsSortBy? = null, offset: Int? = null, limit: Int? = null, type: CommentsSortType? = null,
                       userId: CyberName? = null, permlink: String? = null, communityId: String? = null,
                       communityAlias: String? = null, parentComment: ParentComment? = null, resolveNestedComments: Boolean? = null): Either<GetCommentsResponseRaw, ApiResponseError> =
            apiService.getCommentsRaw(sortBy?.toString(), offset, limit, type?.toString(), userId?.name, permlink,
                    communityId, communityAlias, parentComment, resolveNestedComments)

    fun getUserProfile(user: CyberName?, userName: String?): Either<GetProfileResult, ApiResponseError> =
            apiService.getProfile(user?.name, userName)

    fun getBalance(userId: CyberName) =
            apiService.getWalletBalance(userId)

    fun getTokensInfo(codes: List<CyberSymbolCode>) = apiService.getTokensInfo(codes.map { it.value })

    fun getLeaders(communityId: String, limit: Int? = null, offset: Int? = null): Either<LeadersResponse, ApiResponseError> = apiService.getLeaders(communityId, limit, offset, null)

    fun getBlacklistedUsers(userId: CyberName): Either<BlacklistedUsersResponse, ApiResponseError> =
            apiService.getBlacklistedUsers(userId)

    fun getBlacklistedCommunities(userId: CyberName): Either<BlacklistedCommunitiesResponse, ApiResponseError> = apiService.getBlacklistedCommunities(userId)

    fun getSubscribers(userId: CyberName?,
                       communityId: String?,
                       limit: Int? = null,
                       offset: Int? = null): Either<SubscribedUsersResponse, ApiResponseError> = apiService.getSubscribers(userId, communityId, limit, offset)

    fun getUserSubscriptions(ofUser: CyberName, limit: Int? = null, offset: Int? = null): Either<UserSubscriptionsResponse, ApiResponseError> = apiService.getUserSubscriptions(ofUser, limit, offset)

    fun getCommunitySubscriptions(ofUser: CyberName, limit: Int? = null, offset: Int? = null): Either<CommunitySubscriptionsResponse, ApiResponseError> = apiService.getCommunitySubscriptions(ofUser, limit, offset)

    fun getReports(communityIds: List<String>?,
                   status: ReportsRequestStatus?,
                   contentType: ReportRequestContentType?,
                   sortBy: ReportsRequestTimeSort? = null,
                   limit: Int? = null,
                   offset: Int? = null): Either<GetReportsResponse, ApiResponseError> = apiService.getReports(communityIds, status, contentType, sortBy, limit, offset)

    fun getReportsRaw(communityIds: List<String>?,
                      status: ReportsRequestStatus?,
                      contentType: ReportRequestContentType?,
                      sortBy: ReportsRequestTimeSort? = null,
                      limit: Int? = null,
                      offset: Int? = null): Either<GetReportsResponseRaw, ApiResponseError> = apiService.getReportsRaw(communityIds, status, contentType, sortBy, limit, offset)

    fun suggestNames(text: String): Either<SuggestNameResponse, ApiResponseError> = apiService.suggestNames(text)

    fun getConfig() = apiService.getConfig()

    @JvmOverloads
    fun getNotifications(limit: Int? = 20, beforeThan: String? = null, filter: List<GetNotificationsFilter>? = null): Either<GetNotificationsResponse, ApiResponseError> = apiService.getNotifications(limit, beforeThan, filter)

    @JvmOverloads
    fun getNotificationsSkipUnrecognized(limit: Int? = 20, beforeThan: String? = null, filter: List<GetNotificationsFilter>? = null): Either<GetNotificationsResponse, ApiResponseError> = apiService.getNotificationsSafe(limit, beforeThan, filter)

    fun getNotificationsStatus(): Either<GetNotificationStatusResponse, ApiResponseError> = apiService.getNotificationsStatus()

    fun markAllNotificationAsViewed(until: String): Either<ResultOk, ApiResponseError> = apiService.markAllNotificationAsViewed(until)

    fun subscribeOnNotifications(): Either<ResultOk, ApiResponseError> = apiService.subscribeOnNotifications()

    fun unSubscribeFromNotifications(): Either<ResultOk, ApiResponseError> = apiService.unSubscribeFromNotifications()

    fun getStateBulk(posts: List<UserAndPermlinkPair>): Either<GetStateBulkResponse, ApiResponseError> = apiService.getStateBulk(posts)

    @JvmOverloads
    fun getTransferHistory(userId: CyberName, direction: TransferHistoryDirection? = null, transferType: TransferHistoryTransferType? = null,
                           symbol: CyberSymbolCode? = null, rewards: String? = null, limit: Int? = null, offset: Int? = null): Either<GetTransferHistoryResponse, ApiResponseError> = apiService.getTransferHistory(userId, direction, transferType, symbol, rewards, limit, offset)

    fun getBuyPrice(pointSymbol: CyberSymbolCode, quantity: WalletQuantity): Either<GetWalletBuyPriceResponse, ApiResponseError> = apiService.getBuyPrice(pointSymbol, quantity)

    fun getSellPrice(quantity: WalletQuantity): Either<GetWalletSellPriceResponse, ApiResponseError> = apiService.getSellPrice(quantity)

    /** method will block thread until [blockNum] would consumed by prism services
     * @param blockNum num of block to wait
     * @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     */
    fun waitForABlock(blockNum: Long): Either<ResultOk, ApiResponseError> = apiService.waitBlock(blockNum)

    /** method will block thread until [transactionId] would be consumed by prism services. Old transaction are not stored in services.
     * @param transactionId userId of transaction to wait
     * @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     */
    fun waitForTransaction(transactionId: String): Either<ResultOk, ApiResponseError> = apiService.waitForTransaction(transactionId)


    fun <T : Any> pushTransactionWithProvidedBandwidth(chainId: String,
                                                       transactionAbi: TransactionAbi,
                                                       signature: String,
                                                       traceType: Class<T>): Either<TransactionCommitted<T>, GolosEosError> = apiService.pushTransactionWithProvidedBandwidth(chainId, transactionAbi, signature, traceType)

    fun getRegistrationState(
            phone: String?
    ): Either<UserRegistrationStateResult, ApiResponseError> =
            apiService.getRegistrationStateOf(userId = null, phone = phone)


    /** method leads to sending sms code to user's [phone]. proper [testingPass] makes backend to omit this check
     *  @param captcha capthc string
     *  @param phone  of user for sending sms verification code
     *  @param testingPass pass to omit cpatcha and phone checks
     *  @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
     *  @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     * */

    fun firstUserRegistrationStep(captcha: String?, phone: String, testingPass: String?) =
            apiService.firstUserRegistrationStep(captcha, phone, testingPass)

    /** method used to verify [phone] by sent [code] through sms. Second step of registration
     *  @param code sms code sent to [phone]
     *  @param phone  of user
     *  @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
     *  @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     * */
    fun verifyPhoneForUserRegistration(phone: String, code: Int) =
            apiService.verifyPhoneForUserRegistration(phone, code)

    /** method used to connect verified [user] name with [phone]. Third step of registration
     *  @param user name to associate with [phone]
     *  @param phone verified phone
     *  @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
     *  @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     * */
    fun setVerifiedUserName(user: String, phone: String) = apiService.setVerifiedUserName(user, phone)

    /** method used to finalize registration of user in cyberway blockchain. Final step of registration
     *  @param userName name of user
     *  @param owner public owner key of user
     *  @param active public active key of user
     *  @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
     *  @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     * */
    fun writeUserToBlockChain(
            phone: String,
            userId: String,
            userName: String,
            owner: String,
            active: String
    ) = apiService.writeUserToBlockchain(phone, userId, userName, owner, active)


    /** method used to resend sms code to user during phone verification
     *  @param forUser name of user
     *  @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
     *  @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     * */
    fun resendSmsCode(forUser: String, @Suppress("UNUSED_PARAMETER") unused: Int = 0) = apiService.resendSmsCode(forUser, null)

    /** method used to resend sms code to user during phone verification
     *  @param phone phone of user to verify
     *  @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
     *  @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     * */
    fun resendSmsCode(phone: String) = apiService.resendSmsCode(null, phone)

    fun onBoardingCommunitySubscriptions(userId: CyberName, communityIds: List<String>) = apiService.onBoardingCommunitySubscriptions(userId.name, communityIds)


    /**part of auth process. It consists of 3 steps:
     * 1. getting secret string using method [getAuthSecret]
     * 2. signing it with [StringSigner]
     * 3. sending result using [authWithSecret] method
     *  @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     * */
    fun getAuthSecret(): Either<AuthSecret, ApiResponseError> = apiService.getAuthSecret()

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
                       signedSecret: String): Either<AuthResult, ApiResponseError> = apiService.authWithSecret(userName, secret, signedSecret)

    /**disconnects from microservices, effectively unaithing
     * Method will result  throwing all pending socket requests.
     * */
    fun unAuth() = apiService.unAuth()

    fun quickSearch(queryString: String, limit: Int?, entities: List<SearchableEntities>?): Either<QuickSearchResponse, ApiResponseError> = apiService.quickSearch(queryString, limit, entities)

    fun extendedSearch(queryString: String,
                       profilesSearchRequest: ExtendedRequestSearchItem?,
                       communitiesSearchRequest: ExtendedRequestSearchItem?,
                       postsSearchRequest: ExtendedRequestSearchItem?): Either<ExtendedSearchResponse, ApiResponseError> = apiService.extendedSearch(queryString, profilesSearchRequest, communitiesSearchRequest, postsSearchRequest)


    /** method for fetching  account of some user
     * @param user name of user, which account is  fetched
     * @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     */
    fun getUserAccount(user: CyberName): Either<UserProfile, GolosEosError> {
        return try {
            val accResponse = chainApi.getAccount(AccountName(user.name)).blockingGet()
            if (accResponse.isSuccessful) {
                val acc = accResponse.body()!!
                return Either.Success(
                        UserProfile(acc.account_name,
                                acc.head_block_num,
                                acc.head_block_time,
                                acc.privileged,
                                acc.last_code_update,
                                acc.created,
                                acc.core_liquid_balance,
                                acc.ram_quota,
                                acc.net_weight,
                                acc.cpu_weight,
                                acc.ram_usage,
                                acc.permissions.map { accountPermission ->
                                    UserProfile.AccountPermission(accountPermission.perm_name, accountPermission.parent,
                                            UserProfile.AccountRequiredAuth(
                                                    accountPermission.required_auth.threshold,
                                                    accountPermission.required_auth.keys.map { accountKey ->
                                                        UserProfile.AccountKey(accountKey.key, accountKey.weight)
                                                    },
                                                    accountPermission.required_auth.accounts.map { accountAuth ->
                                                        UserProfile.AccountAuth(accountAuth.permission.run {
                                                            UserProfile.AccountAuthPermission(actor, permission)
                                                        }, accountAuth.weight)
                                                    }
                                            ))
                                })
                )
            } else {
                return Either.Failure(
                        moshi.adapter(GolosEosError::class.java).fromJson(
                                accResponse.errorBody()!!.string()
                        )!!
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Either.Failure(
                    GolosEosError(0, e.message ?: e.localizedMessage
                    ?: "", GolosEosError.Error(0, "", "", emptyList()))
            )

        }
    }

    /** method for uploading images.
     * @param file file with image
     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
     * */
    fun uploadImage(file: File): Either<String, GolosEosError> {
        return try {
            Either.Success(chainApi.uploadImage(file).blockingGet())
        } catch (e: java.lang.Exception) {
            Either.Failure(
                    GolosEosError(0, e.message ?: e.localizedMessage
                    ?: "", GolosEosError.Error(0, "", "", emptyList()))
            )
        }

    }

    /** cyber.token@transfer action
     * */
    @JvmOverloads
    fun exchange(
            to: CyberName,
            amount: String,
            currency: String,
            memo: String = "",
            bandWidthRequest: BandWidthRequest? = null,
            key: String = keyStorage.getActiveAccountKeys().find { it.first == AuthType.ACTIVE }!!.second,
            from: CyberName = keyStorage.getActiveAccount()
    ): Either<TransactionCommitted<TransferCyberTokenStruct>, GolosEosError> {

        // if (!amount.matches("([0-9]+\\.[0-9]{3})".toRegex())) throw IllegalArgumentException("wrong currency format. Must have 3 points precision, like 12.000 or 0.001")

        return pushTransaction<TransferCyberTokenStruct>(TransferCyberTokenAction(
                TransferCyberTokenStruct(
                        from, to,
                        CyberAsset("$amount $currency"), memo
                )
        ), TransactionAuthorizationAbi(from.name, "active"), key, bandWidthRequest, null)

    }

    /** c.point@transfer action
     * */
    @JvmOverloads
    fun transfer(
            to: CyberName,
            amount: String,
            currency: String,
            memo: String = "",
            bandWidthRequest: BandWidthRequest? = null,
            key: String = keyStorage.getActiveAccountKeys().find { it.first == AuthType.ACTIVE }!!.second,
            from: CyberName = keyStorage.getActiveAccount()
    ): Either<TransactionCommitted<TransferCPointStruct>, GolosEosError> {

        //  if (!amount.matches("([0-9]+\\.[0-9]{3})".toRegex())) throw IllegalArgumentException("wrong currency format. Must have 3 points precision, like 12.000 or 0.001")

        return pushTransaction(TransferCPointAction(
                TransferCPointStruct(
                        from, to,
                        CyberAsset("$amount $currency"), memo
                )
        ), TransactionAuthorizationAbi(from.name, "active"), key, bandWidthRequest, null)
    }

    /** cyber.token@open action
     * */
    fun openBalance(symbol: CyberSymbol,
                    ramPayer: CyberName,
                    owner: CyberName = keyStorage.getActiveAccount(),
                    bandWidthRequest: BandWidthRequest? = null,
                    key: String = keyStorage.getActiveAccountKeys().find { it.first == AuthType.ACTIVE }!!.second): Either<TransactionCommitted<OpenCyberTokenStruct>, GolosEosError> {

        return pushTransaction<OpenCyberTokenStruct>(OpenCyberTokenAction(
                OpenCyberTokenStruct(
                        owner, symbol, ramPayer
                )
        ), TransactionAuthorizationAbi(owner.name, "active"), key, bandWidthRequest, null)
    }

    /**method closes all connections, pools, executors etc. After that instance is useless
     * */
    fun shutdown() {
        synchronized(this) {
            chainApi.shutDown()
            apiService.shutDown()
        }
    }
}