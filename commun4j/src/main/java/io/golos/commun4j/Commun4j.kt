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
import io.golos.commun4j.abi.implementation.c.social.*
import io.golos.commun4j.abi.implementation.cyber.domain.NewusernameCyberDomainAction
import io.golos.commun4j.abi.implementation.cyber.domain.NewusernameCyberDomainStruct
import io.golos.commun4j.abi.implementation.cyber.token.OpenCyberTokenAction
import io.golos.commun4j.abi.implementation.cyber.token.OpenCyberTokenStruct
import io.golos.commun4j.abi.implementation.cyber.token.TransferCyberTokenAction
import io.golos.commun4j.abi.implementation.cyber.token.TransferCyberTokenStruct
import io.golos.commun4j.chain.actions.transaction.TransactionPusher
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.core.crypto.EosPrivateKey
import io.golos.commun4j.http.rpc.RpcServerMessage
import io.golos.commun4j.http.rpc.RpcServerMessageCallback
import io.golos.commun4j.http.rpc.model.account.request.AccountName
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.model.*
import io.golos.commun4j.services.CyberServicesApiService
import io.golos.commun4j.services.model.ApiService
import io.golos.commun4j.sharedmodel.*
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
        private val apiService: ApiService = CyberServicesApiService(config, serverMessageCallback = serverMessageCallback)) : ApiService by apiService {

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
    ): Either<TransactionCommitted<VoteCGalleryStruct>, GolosEosError> {


        return pushTransaction(UpvoteCGalleryAction(VoteCGalleryStruct(communCode,
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
    ): Either<TransactionCommitted<VoteCGalleryStruct>, GolosEosError> {


        return pushTransaction(DownvoteCGalleryAction(VoteCGalleryStruct(communCode,
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
    ): Either<TransactionCommitted<PinCSocialStruct>, GolosEosError> {


        return pushTransaction(UnpinCSocialAction(PinCSocialStruct(
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
    ): Either<TransactionCommitted<FollowCListStruct>, GolosEosError> {

        return pushTransaction(UnfollowCListAction(FollowCListStruct(
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
    ): Either<TransactionCommitted<BlockCSocialStruct>, GolosEosError> {
        return pushTransaction(UnblockCSocialAction(BlockCSocialStruct(
                blocker, blocking
        )), TransactionAuthorizationAbi(blocker.name, "active"), key, bandWidthRequest, clientAuthRequest)
    }

    /** c.list@ban action
     * */
    @JvmOverloads
    fun banUserFromCommunity(communCode: CyberSymbolCode,
                             leader: CyberName,
                             reason: String,
                             account: CyberName,
                             actor: CyberName = keyStorage.getActiveAccount(),
                             bandWidthRequest: BandWidthRequest? = null,
                             clientAuthRequest: ClientAuthRequest? = null,
                             key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<BanCListStruct>, GolosEosError> {
        return pushTransaction<BanCListStruct>(BanCListAction(BanCListStruct(
                communCode, leader, account, reason
        )), TransactionAuthorizationAbi(actor.name, "active"), key, bandWidthRequest, clientAuthRequest)
    }

    /** c.list@unban action
     * */
    @JvmOverloads
    fun unBanUserFromCommunity(communCode: CyberSymbolCode,
                               leader: CyberName,
                               reason: String,
                               account: CyberName,
                               actor: CyberName = keyStorage.getActiveAccount(),
                               bandWidthRequest: BandWidthRequest? = null,
                               clientAuthRequest: ClientAuthRequest? = null,
                               key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<BanCListStruct>, GolosEosError> {
        return pushTransaction(UnbanCListAction(BanCListStruct(
                communCode, leader, account, reason
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
    ): Either<TransactionCommitted<FollowCListStruct>, GolosEosError> {


        return pushTransaction(HideCListAction(FollowCListStruct(
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
    ): Either<TransactionCommitted<FollowCListStruct>, GolosEosError> {
        return pushTransaction(UnhideCListAction(FollowCListStruct(
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
            bandWidthRequest: BandWidthRequest? = null,
            clientAuthRequest: ClientAuthRequest? = null,
            user: CyberName = keyStorage.getActiveAccount(),
            key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<UpdatemetaCSocialStruct>, GolosEosError> {
        return pushTransaction(UpdatemetaCSocialAction(UpdatemetaCSocialStruct(
                user,
                AccountmetaCSocialStruct(
                        avatarUrl, coverUrl, biography, facebook, telegram, whatsapp, wechat)
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
    ): Either<TransactionCommitted<TransferArgsCPointStruct>, GolosEosError> {

        //  if (!amount.matches("([0-9]+\\.[0-9]{3})".toRegex())) throw IllegalArgumentException("wrong currency format. Must have 3 points precision, like 12.000 or 0.001")

        return pushTransaction(TransferCPointAction(
                TransferArgsCPointStruct(
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