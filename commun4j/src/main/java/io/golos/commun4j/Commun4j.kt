@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.golos.commun4j

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import io.golos.commun4j.abi.implementation.c.gallery.*
import io.golos.commun4j.abi.implementation.c.social.*
import io.golos.commun4j.abi.implementation.cyber.domain.NewusernameCyberDomainAction
import io.golos.commun4j.abi.implementation.cyber.domain.NewusernameCyberDomainStruct
import io.golos.commun4j.abi.implementation.cyber.token.TransferCyberTokenAction
import io.golos.commun4j.abi.implementation.cyber.token.TransferCyberTokenStruct
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.AbiBinaryGenTransactionWriter
import io.golos.commun4j.chain.actions.transaction.TransactionPusher
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.chain.actions.transaction.misc.ProvideBandwichAbi
import io.golos.commun4j.core.crypto.EosPrivateKey
import io.golos.commun4j.http.rpc.model.ApiResponseError
import io.golos.commun4j.http.rpc.model.account.request.AccountName
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.model.*
import io.golos.commun4j.model.FeedTimeFrame
import io.golos.commun4j.services.CyberServicesApiService
import io.golos.commun4j.services.model.*
import io.golos.commun4j.sharedmodel.*
import io.golos.commun4j.utils.StringSigner
import io.golos.commun4j.utils.toCyberName
import net.gcardone.junidecode.Junidecode
import java.io.File
import java.net.SocketTimeoutException
import java.util.*
import java.util.concurrent.Callable

open class Commun4j @JvmOverloads constructor(
        val config: Commun4jConfig,
        chainApiProvider: ChainApiProvider? = null,
        val keyStorage: KeyStorage = KeyStorage(),
        private val apiService: ApiService = CyberServicesApiService(config)) {

    private val staleTransactionErrorCode = 3080006
    private val resourceExceedErrorCode = 3080004
    private val transactionPusher: TransactionPusherImpl = TransactionPusherImpl(config)
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

    init {
        chainApi = chainApiProvider?.provide()
                ?: io.golos.commun4j.GolosEosConfiguratedApi(config, moshi).provide()
    }


    private fun isStaleError(callResult: Either<out Any?, GolosEosError>): Boolean {
        return callResult is Either.Failure
                && (callResult.value.error?.code == staleTransactionErrorCode
                || callResult.value.error?.code == resourceExceedErrorCode)
    }

    private fun formatPostPermlink(permlinkToFormat: String): String {
        val workingCopy =
                if (permlinkToFormat.length < 12) permlinkToFormat + UUID.randomUUID().toString() else permlinkToFormat
        var unicodePermlink = Junidecode.unidecode(workingCopy).toLowerCase().replace(Regex("((?![a-z0-9-]).)"), "")
        if (unicodePermlink.length < 12) unicodePermlink += (UUID.randomUUID().toString().toLowerCase())
        if (unicodePermlink.length > 256) unicodePermlink.substring(0, 257)
        return unicodePermlink
    }

    // sometimes blockain refuses to transact proper transaction due to some inner problems.
    //if it returns TimeoutException - then i try to push transaction again
    private fun <T> callTilTimeoutExceptionVanishes(
            callable: Callable<Either<TransactionCommitted<T>,
                    GolosEosError>>
    ): Either<TransactionCommitted<T>, GolosEosError> {
        var result: Either<TransactionCommitted<T>, GolosEosError>
        do {
            result = callable.call()
        } while (isStaleError(result))

        return result
    }

    /**function tries to resolve canonical name from domain (..@golos for example) or username
     * @param name userName to resolve to
     * @param appName app in which domain this name is. Currently there is 'cyber' and 'gls' apps
     * @throws IllegalArgumentException if name doesn't exist
     * */
    fun resolveCanonicalCyberName(name: String) = apiService.resolveProfile(name)


    private inline fun <reified T : Any> pushTransaction(
            actions: List<ActionAbi>,
            actorKey: String,
            bandWidthRequest: BandWidthRequest?,
            clientAuthRequest: ClientAuthRequest?): Either<TransactionCommitted<T>, GolosEosError> {
        var resolvedActions = actions
        if (clientAuthRequest != null) {
            resolvedActions = resolvedActions.map {
                ActionAbi(it.account,
                        it.name,
                        it.authorization + TransactionAuthorizationAbi("c.gallery", "clients"),
                        it.data)
            }
        }

        return if (bandWidthRequest?.source == BandWidthSource.COMN_SERVICES) {

            val signedTrx =
                    TransactionPusher.createSignedTransaction(resolvedActions + createProvideBw(resolvedActions.first().authorization.first().actor.toCyberName(), bandWidthRequest.actor),
                            listOf(EosPrivateKey(actorKey)), config.blockChainHttpApiUrl,
                            config.logLevel, config.httpLogger)

            if (signedTrx is Either.Failure) return Either.Failure(signedTrx.value)

            signedTrx as Either.Success
            return pushTransactionWithProvidedBandwidth(signedTrx.value.usedChainInfo.chain_id,
                    signedTrx.value.transaction,
                    signedTrx.value.signedTransactionSignatures.first(),
                    T::class.java)

        } else transactionPusher.pushTransaction(resolvedActions.let {
            if (bandWidthRequest != null) it + createProvideBw(resolvedActions.first().authorization.first().actor.toCyberName(), bandWidthRequest.actor)
            else it
        },
                EosPrivateKey(actorKey),
                T::class.java,
                if (clientAuthRequest != null && bandWidthRequest == null) listOf(clientAuthRequest.key) else null,
                bandWidthRequest)
    }

    private inline fun <reified T : Any> pushTransaction(
            action: ActionAbi,
            key: String,
            bandWidthRequest: BandWidthRequest?,
            clientAuthRequest: ClientAuthRequest?): Either<TransactionCommitted<T>, GolosEosError> {
        return pushTransaction(listOf(action),
                key,
                bandWidthRequest,
                clientAuthRequest)
    }


//    /** method for account creation.
//     * currently consists of 5 steps:
//     * 1. create eos account
//     * 2. open vesting balance [openVestingBalance]
//     * 3. open token balance [openTokenBalance]
//     * 4. issuing vesting to new user [issueTokens]
//     * if one of this steps fails - you need do it manually, to fully init new user.
//     * @param newAccountName account name of new account. must be [CyberName] compatible. Format - "[a-z0-5.]{0,12}"
//     * @param newAccountMasterPassword master password for generating keys for newly created account.
//     * method uses [AuthUtils.generatePrivateWiFs] for generating private key - so can you. Also
//     * [AuthUtils.generatePublicWiFs] for acquiring public keys
//     *  @param actor account name of creator
//     * @param cyberCreatePermissionKey key of "gls" for "newaccount" action with "active" permission
//     * @throws IllegalStateException if method failed to open vesting or token balance, issue tokens or transfer it to "gls.vesting
//     *  @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
//     * */

//    @JvmOverloads
//    fun createAccount(
//            newAccountName: String,
//            newAccountMasterPassword: String,
//            actor: CyberName,
//            cyberCreatePermissionKey: String,
//            bandWidthRequest: BandWidthRequest? = null
//    ): Either<TransactionCommitted<NewaccountCyberStruct>, GolosEosError> {
//        CyberName(newAccountName)
//
//        val keys = AuthUtils.generatePublicWiFs(newAccountName, newAccountMasterPassword, AuthType.values())
//
//        val callable = Callable {
//            pushTransaction<NewaccountCyberStruct>(NewaccountCyberAction(NewaccountCyberStruct(
//                    actor,
//                    CyberName(newAccountName),
//                    AuthorityCyberStruct(
//                            1,
//                            listOf(KeyWeightCyberStruct(EosPublicKey(keys[AuthType.OWNER]!!), 1)),
//                            emptyList(), emptyList()
//                    ),
//                    AuthorityCyberStruct(
//                            1,
//                            listOf(KeyWeightCyberStruct(EosPublicKey(keys[AuthType.ACTIVE]!!), 1)),
//                            emptyList(),
//                            emptyList()
//                    )
//            ))
//                    .toActionAbi(listOf(TransactionAuthorizationAbi(actor.name,
//                            "active"))),
//
//                    cyberCreatePermissionKey,
//                    bandWidthRequest)
//        }
//        return callTilTimeoutExceptionVanishes(callable)


//        if (createAnswer is Either.Failure) return createAnswer
//
//        val openTokenResult = openTokenBalance(newAccountName.toCyberName(), cyberCreatePermissionKey, actor)
//
//        if (openTokenResult is Either.Failure) throw IllegalStateException(
//                "error initializing of account $newAccountName" +
//                        "during openTokenBalance()"
//        )
//        val issueVestingResult =
//                issueVesting(newAccountName.toCyberName(), cyberCreatePermissionKey, "3.000 GOLOS")
//
//        if (issueVestingResult is Either.Failure) throw IllegalStateException(
//                "error initializing of account $newAccountName\n" +
//                        "during issueVesting()"
//        )
//
//        return createAnswer
    //}

//    /** method for opening vesting balance of account. used in [createAccount] as one of the steps of
//     * new account creation
//     * @param forUser account name
//     * @param cyberKey key of "cyber" with "createuser" permission
//     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
//     * */
//
//    fun openVestingBalance(
//            forUser: CyberName,
//            cyberKey: String,
//            actor: CyberName,
//            bandWidthRequest: BandWidthRequest? = null
//    ) = openBalance(forUser, UserBalance.VESTING, actor, cyberKey, bandWidthRequest)

//    /** method for opening token balance of account. used in [createAccount] as one of the steps of
//     * new account creation
//     * @param forUser account name
//     * @param cyberCreatePermissionKey key of "cyber" with "createuser" permission
//     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
//     * */
//
//    fun openTokenBalance(
//            forUser: CyberName,
//            actor: CyberName,
//            cyberCreatePermissionKey: String,
//            symbol: CyberSymbol,
//            bandWidthRequest: BandWidthRequest? = null
//    ): Either<TransactionCommitted<OpenCyberTokenStruct>, GolosEosError> {
//
//        val setUserNameCallable = Callable {
//            pushTransaction<OpenCyberTokenStruct>(OpenCyberTokenAction(
//                    OpenCyberTokenStruct(forUser, symbol, actor)
//            ).toActionAbi(
//                    listOf(TransactionAuthorizationAbi(actor.name, "active"))
//            ), cyberCreatePermissionKey, bandWidthRequest)
//        }
//        return callTilTimeoutExceptionVanishes(setUserNameCallable)
//    }

//    /** method for issuing vesting for [forUser] recipient. Also, used as part of new account creation in [createAccount]
//     * @param forUser account name
//     * @param issuerKey key of "gls" with "issue" permission
//     * @param amount amount of tokens to issue.  Must have 3 points precision, like 12.000 or 0.001
//     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
//     * */
//    fun issueVesting(
//            forUser: CyberName,
//            issuerKey: String,
//            amount: String,
//            memo: String = "",
//            bandWidthRequest: BandWidthRequest? = null
//    ): Either<TransactionCommitted<Any>, GolosEosError> {
//
//        val issuerTokenCallable = Callable {
//
//            val actionAbis = ArrayList<ActionAbi>()
//
//            val writer = createBinaryConverter()
//            val issueRequest = IssueRequestAbi(CyberContracts.GLS.toString().toCyberName(), amount, memo)
//            val result = writer.squishIssueRequestAbi(issueRequest)
//
//            if (config.logLevel == LogLevel.BODY) config.httpLogger
//                    ?.log("issue request  = ${moshi.adapter(IssueRequestAbi::class.java).toJson(issueRequest)}")
//
//            var hex = result.toHex()
//            val issueAbi = ActionAbi(
//                    CyberContracts.CYBER_TOKEN.toString(), CyberActions.ISSUE.toString(),
//                    listOf(TransactionAuthorizationAbi(CyberContracts.GLS.toString(), "issue")), hex
//            )
//            if (config.logLevel == LogLevel.BODY) config.httpLogger
//                    ?.log("issue transaction = ${moshi.adapter(ActionAbi::class.java).toJson(issueAbi)}")
//
//            actionAbis.add(issueAbi)
//
//            hex = createBinaryConverter().squishMyTransferArgsAbi(
//                    MyTransferArgsAbi(CyberContracts.GLS.toString(),
//                            CyberContracts.VESTING.toString(),
//                            amount,
//                            "send to: ${forUser.name};")
//            ).toHex()
//
//            actionAbis.add(
//                    ActionAbi(
//                            CyberContracts.CYBER_TOKEN.toString(), CyberActions.TRANSFER.toString(),
//                            listOf(TransactionAuthorizationAbi(CyberContracts.GLS.toString(), "issue")), hex
//                    )
//            )
//            transactionPusher.pushTransaction(actionAbis, EosPrivateKey(issuerKey),
//                    Any::class.java, bandWidthRequest)
//        }
//
//        return callTilTimeoutExceptionVanishes(issuerTokenCallable)
//    }


//    /** method for issuing tokens for [forUser] recipient. Also, used as part of new account creation in [createAccount]
//     * @param forUser account name
//     * @param issuerKey key of "gls" with "issue" permission
//     * @param amount amount of tokens to issue.  Must have 3 points precision, like 12.000 or 0.001
//     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
//     * */
//    fun issueTokens(
//            forUser: CyberName,
//            issuerKey: String,
//            amount: String,
//            memo: String = "",
//            bandWidthRequest: BandWidthRequest? = null
//    ): Either<TransactionCommitted<Any>, GolosEosError> {
//
//        val issuerTokenCallable = Callable {
//
//            val actionAbis = ArrayList<ActionAbi>()
//
//            val writer = createBinaryConverter()
//            val issueRequest = IssueRequestAbi(CyberContracts.GLS.toString().toCyberName(), amount, memo)
//            val result = writer.squishIssueRequestAbi(issueRequest)
//
//            if (config.logLevel == LogLevel.BODY) config.httpLogger
//                    ?.log("issue request  = ${moshi.adapter(IssueRequestAbi::class.java).toJson(issueRequest)}")
//
//            var hex = result.toHex()
//            val issueAbi = ActionAbi(
//                    CyberContracts.CYBER_TOKEN.toString(), CyberActions.ISSUE.toString(),
//                    listOf(TransactionAuthorizationAbi(CyberContracts.GLS.toString(), "issue")), hex
//            )
//            if (config.logLevel == LogLevel.BODY) config.httpLogger
//                    ?.log("issue transaction = ${moshi.adapter(ActionAbi::class.java).toJson(issueAbi)}")
//
//            actionAbis.add(issueAbi)
//
//            hex = createBinaryConverter().squishMyTransferArgsAbi(
//                    MyTransferArgsAbi(CyberContracts.GLS.toString(), forUser.name, amount, memo)
//            ).toHex()
//
//            actionAbis.add(
//                    ActionAbi(
//                            CyberContracts.CYBER_TOKEN.toString(), CyberActions.TRANSFER.toString(),
//                            listOf(TransactionAuthorizationAbi(CyberContracts.GLS.toString(), "issue")), hex
//                    )
//            )
//            transactionPusher.pushTransaction(actionAbis,
//                    EosPrivateKey(issuerKey), Any::class.java, bandWidthRequest)
//
//        }
//
//        return callTilTimeoutExceptionVanishes(issuerTokenCallable)
//    }

    /**method sets  nickname for active user. Method assumes, that there is some active user in [keyStorage]
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
        val setUserNameCallable = Callable {
            pushTransaction<NewusernameCyberDomainStruct>(NewusernameCyberDomainAction(
                    NewusernameCyberDomainStruct(owner, forUser, newUserName)
            ).toActionAbi(
                    listOf(TransactionAuthorizationAbi(owner.name, "active"))
            ), creatorKey, bandWidthRequest, null)
        }
        return callTilTimeoutExceptionVanishes(setUserNameCallable)
    }

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

        val newPostCallable = Callable {
            pushTransaction<CreateCGalleryStruct>(CreateCGalleryAction(
                    CreateCGalleryStruct(communCode,
                            MssgidCGalleryStruct(author, formatPostPermlink(header)),
                            MssgidCGalleryStruct(CyberName(), ""),
                            header,
                            body,
                            tags,
                            metadata,
                            weight)
            ).toActionAbi(
                    listOf(TransactionAuthorizationAbi(author.name, "active"))
            ), authorKey, bandWidthRequest, clientAuthRequest)
        }
        return callTilTimeoutExceptionVanishes(newPostCallable)
    }

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

        val newPostCallable = Callable {
            pushTransaction<CreateCGalleryStruct>(CreateCGalleryAction(
                    CreateCGalleryStruct(communCode,
                            MssgidCGalleryStruct(author, formatPostPermlink(header)),
                            parentMssgId,
                            header,
                            body,
                            tags,
                            metadata,
                            weight)
            ).toActionAbi(
                    listOf(TransactionAuthorizationAbi(author.name, "active"))
            ), authorKey, bandWidthRequest, clientAuthRequest)
        }
        return callTilTimeoutExceptionVanishes(newPostCallable)
    }

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

        val newPostCallable = Callable {
            pushTransaction<UpdateCGalleryStruct>(UpdateCGalleryAction(
                    UpdateCGalleryStruct(communCode,
                            messageId,
                            header,
                            body,
                            tags,
                            metadata)
            ).toActionAbi(
                    listOf(TransactionAuthorizationAbi(author.name, "active"))
            ), authorKey, bandWidthRequest, clientAuthRequest)
        }
        return callTilTimeoutExceptionVanishes(newPostCallable)
    }

    @JvmOverloads
    fun deletePostOrComment(
            messageId: MssgidCGalleryStruct,
            communCode: CyberSymbolCode,
            bandWidthRequest: BandWidthRequest? = null,
            clientAuthRequest: ClientAuthRequest? = null,
            author: CyberName = keyStorage.getActiveAccount(),
            authorKey: String = keyStorage.getActiveKeyOfActiveAccount()): Either<TransactionCommitted<RemoveCGalleryStruct>, GolosEosError> {

        val newPostCallable = Callable {
            pushTransaction<RemoveCGalleryStruct>(RemoveCGalleryAction(
                    RemoveCGalleryStruct(communCode,
                            messageId)
            ).toActionAbi(
                    listOf(TransactionAuthorizationAbi(author.name, "active"))
            ), authorKey, bandWidthRequest, clientAuthRequest)
        }
        return callTilTimeoutExceptionVanishes(newPostCallable)
    }


    @JvmOverloads
    fun upVote(communCode: CyberSymbolCode,
               messageId: MssgidCGalleryStruct,
               weight: Short,
               bandWidthRequest: BandWidthRequest? = null,
               clientAuthRequest: ClientAuthRequest? = null,
               voter: CyberName = keyStorage.getActiveAccount(),
               key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<VoteCGalleryStruct>, GolosEosError> {

        val newPostCallable = Callable {
            pushTransaction<VoteCGalleryStruct>(UpvoteCGalleryAction(VoteCGalleryStruct(communCode,
                    voter, messageId, weight)
            ).toActionAbi(
                    listOf(TransactionAuthorizationAbi(voter.name, "active"))
            ), key, bandWidthRequest, clientAuthRequest)
        }
        return callTilTimeoutExceptionVanishes(newPostCallable)
    }

    @JvmOverloads
    fun downVote(communCode: CyberSymbolCode,
                 messageId: MssgidCGalleryStruct,
                 weight: Short,
                 bandWidthRequest: BandWidthRequest? = null,
                 clientAuthRequest: ClientAuthRequest? = null,
                 voter: CyberName = keyStorage.getActiveAccount(),
                 key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<VoteCGalleryStruct>, GolosEosError> {

        val newPostCallable = Callable {
            pushTransaction<VoteCGalleryStruct>(DownvoteCGalleryAction(VoteCGalleryStruct(communCode,
                    voter, messageId, weight)
            ).toActionAbi(
                    listOf(TransactionAuthorizationAbi(voter.name, "active"))
            ), key, bandWidthRequest, clientAuthRequest)
        }
        return callTilTimeoutExceptionVanishes(newPostCallable)
    }

    @JvmOverloads
    fun unVote(communCode: CyberSymbolCode,
               messageId: MssgidCGalleryStruct,
               bandWidthRequest: BandWidthRequest? = null,
               clientAuthRequest: ClientAuthRequest? = null,
               voter: CyberName = keyStorage.getActiveAccount(),
               key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<UnvoteCGalleryStruct>, GolosEosError> {

        val newPostCallable = Callable {
            pushTransaction<UnvoteCGalleryStruct>(UnvoteCGalleryAction(UnvoteCGalleryStruct(
                    communCode, voter, messageId
            )).toActionAbi(
                    listOf(TransactionAuthorizationAbi(voter.name, "active"))
            ), key, bandWidthRequest, clientAuthRequest)
        }
        return callTilTimeoutExceptionVanishes(newPostCallable)
    }

    @JvmOverloads
    fun pinUser(pinning: CyberName,
                bandWidthRequest: BandWidthRequest? = null,
                clientAuthRequest: ClientAuthRequest? = null,
                pinner: CyberName = keyStorage.getActiveAccount(),
                key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<PinCSocialStruct>, GolosEosError> {

        val newPostCallable = Callable {
            pushTransaction<PinCSocialStruct>(PinCSocialAction(PinCSocialStruct(
                    pinner, pinning
            )).toActionAbi(
                    listOf(TransactionAuthorizationAbi(pinner.name, "active"))
            ), key, bandWidthRequest, clientAuthRequest)
        }
        return callTilTimeoutExceptionVanishes(newPostCallable)
    }

    @JvmOverloads
    fun unpinUser(unpinning: CyberName,
                  bandWidthRequest: BandWidthRequest? = null,
                  clientAuthRequest: ClientAuthRequest? = null,
                  pinner: CyberName = keyStorage.getActiveAccount(),
                  key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<PinCSocialStruct>, GolosEosError> {

        val newPostCallable = Callable {
            pushTransaction<PinCSocialStruct>(UnpinCSocialAction(PinCSocialStruct(
                    pinner, unpinning
            )).toActionAbi(
                    listOf(TransactionAuthorizationAbi(pinner.name, "active"))
            ), key, bandWidthRequest, clientAuthRequest)
        }
        return callTilTimeoutExceptionVanishes(newPostCallable)
    }

    @JvmOverloads
    fun block(blocking: CyberName,
              bandWidthRequest: BandWidthRequest? = null,
              clientAuthRequest: ClientAuthRequest? = null,
              blocker: CyberName = keyStorage.getActiveAccount(),
              key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<BlockCSocialStruct>, GolosEosError> {

        val newPostCallable = Callable {
            pushTransaction<BlockCSocialStruct>(BlockCSocialAction(BlockCSocialStruct(
                    blocker, blocking
            )).toActionAbi(
                    listOf(TransactionAuthorizationAbi(blocker.name, "active"))
            ), key, bandWidthRequest, clientAuthRequest)
        }
        return callTilTimeoutExceptionVanishes(newPostCallable)
    }

    @JvmOverloads
    fun unBlock(blocking: CyberName,
                bandWidthRequest: BandWidthRequest? = null,
                clientAuthRequest: ClientAuthRequest? = null,
                blocker: CyberName = keyStorage.getActiveAccount(),
                key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<BlockCSocialStruct>, GolosEosError> {

        val newPostCallable = Callable {
            pushTransaction<BlockCSocialStruct>(UnblockCSocialAction(BlockCSocialStruct(
                    blocker, blocking
            )).toActionAbi(
                    listOf(TransactionAuthorizationAbi(blocker.name, "active"))
            ), key, bandWidthRequest, clientAuthRequest)
        }
        return callTilTimeoutExceptionVanishes(newPostCallable)
    }

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

        val newPostCallable = Callable {
            pushTransaction<UpdatemetaCSocialStruct>(UpdatemetaCSocialAction(UpdatemetaCSocialStruct(
                    user,
                    AccountmetaCSocialStruct(
                            avatarUrl, coverUrl, biography, facebook, telegram, whatsapp, wechat
                    )
            )).toActionAbi(
                    listOf(TransactionAuthorizationAbi(user.name, "active"))
            ), key, bandWidthRequest, clientAuthRequest)
        }
        return callTilTimeoutExceptionVanishes(newPostCallable)
    }

    @JvmOverloads
    fun DeleteUserMetadata(
            bandWidthRequest: BandWidthRequest? = null,
            clientAuthRequest: ClientAuthRequest? = null,
            user: CyberName = keyStorage.getActiveAccount(),
            key: String = keyStorage.getActiveKeyOfActiveAccount()
    ): Either<TransactionCommitted<DeletemetaCSocialStruct>, GolosEosError> {

        val newPostCallable = Callable {
            pushTransaction<DeletemetaCSocialStruct>(DeletemetaCSocialAction(DeletemetaCSocialStruct(
                    user
            )).toActionAbi(
                    listOf(TransactionAuthorizationAbi(user.name, "active"))
            ), key, bandWidthRequest, clientAuthRequest)
        }
        return callTilTimeoutExceptionVanishes(newPostCallable)
    }


    fun getCommunitiesList(offset: Int? = null,
                           limit: Int? = null) = apiService.getCommunitiesList(offset, limit)

    fun getCommunity(communityId: String) = apiService.getCommunity(communityId)


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

    fun getUserProfile(user: CyberName?, userName: String?): Either<GetProfileResult, ApiResponseError> =
            apiService.getProfile(user?.name, userName)

    fun getBalance(userId: CyberName) =
            apiService.getBalance(userId.name)

    @JvmOverloads
    fun getTransferHistory(userId: CyberName,
                           direction: TransactionDirection? = null,
                           sequenceKey: String? = null,
                           limit: Int? = null) = apiService.getTransferHistory(userId.name,
            direction?.toString(),
            sequenceKey,
            limit)

    fun getTokensInfo(codes: List<CyberSymbolCode>) = apiService.getTokensInfo(codes.map { it.value })

    fun getLeaders(communityId: String, limit: Int? = null, offset: Int? = null): Either<LeadersResponse, ApiResponseError> = apiService.getLeaders(communityId, limit, offset, null)

//    fun getCommunityBlacklist(communityId: String?, communityAlias: String? = null, offset: Int? = null, limit: Int? = null) =
//            apiService.getCommunityBlacklist(communityId, communityAlias, offset, limit)

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


//    /**get processed embed link for some raw "https://site.com/content" using iframely service
//     * @param forLink raw link of site content
//     * @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
//     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
//     * */
//    fun getEmbedIframely(forLink: String): Either<IFramelyEmbedResult, ApiResponseError> =
//            apiService.getIframelyEmbed(forLink)
//
//    /**get processed embed link for some raw "https://site.com/content" using oembed service
//     * @param forLink raw link of site content
//     * @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
//     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
//     * */
//    fun getEmbedOembed(forLink: String): Either<OEmbedResult, ApiResponseError> = apiService.getOEmdedEmbed(forLink)
//
//
//    /** method subscribes mobile device for push notifications in FCM.
//     * method requires authorization
//     * @param deviceId userId of device or installation.
//     * @param fcmToken token of app installation in FCM.
//     * @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
//     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
//     * */
//    fun subscribeOnMobilePushNotifications(deviceId: String,
//                                           fcmToken: String,
//                                           appName: String = "gls"): Either<ResultOk, ApiResponseError> = apiService.subscribeOnMobilePushNotifications(deviceId, appName, fcmToken)
//
//    /** method unSubscribes mobile device from push notifications in FCM.
//     *  method requires authorization
//     * @param deviceId userId of device or installation.
//     * @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
//     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
//     * */
//    fun unSubscribeOnNotifications(userId: CyberName,
//                                   deviceId: String,
//                                   appName: String = "gls"): Either<ResultOk, ApiResponseError> = apiService.unSubscribeOnNotifications(userId.name, deviceId, appName)
//
//    /**method for setting various settings for user. If any of setting param is null, this settings will not change.
//     * All setting are individual for every [deviceId]
//     * method requires authorization
//     * @param deviceId userId of device or installation.
//     * @param newBasicSettings schema-free settings, used for saving app personalization.
//     * @param newWebNotifySettings settings of online web notifications.
//     * @param newMobilePushSettings settings of mobile push notifications. Uses FCM.
//     * @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
//     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
//     * */
//    fun setUserSettings(deviceId: String,
//                        newBasicSettings: Any?,
//                        newWebNotifySettings: WebShowSettings?,
//                        newMobilePushSettings: MobileShowSettings?,
//                        appName: String = "gls"): Either<ResultOk, ApiResponseError> = apiService.setNotificationSettings(deviceId, appName, newBasicSettings, newWebNotifySettings, newMobilePushSettings)
//
//    /**method for retreiving user setting. Personal for evert [deviceId]
//     * method requires authorization
//     * @param deviceId userId of device or installation.
//     * @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
//     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
//     * */
//    fun getUserSettings(deviceId: String, appName: String = "gls"): Either<UserSettings, ApiResponseError> = apiService.getNotificationSettings(deviceId, appName)
//
//    /**method for retreiving history of notifications.
//     * method requires authorization
//     * @param userProfile name of user which notifications to retreive.
//     * @param afterId userId of next page of events. Set null if you want first page.
//     * @param limit number of event to retreive
//     * @param markAsViewed set true, if you want to set all retreived notifications as viewed
//     * @param freshOnly set true, if you want get only fresh notifcaitons
//     * @param types list of types of notifcaitons you want to get
//     * @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
//     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
//     * */
//    fun getEvents(userProfile: String,
//                  afterId: String?,
//                  limit: Int?,
//                  markAsViewed: Boolean?,
//                  freshOnly: Boolean?,
//                  types: List<EventType>,
//                  appName: String = "gls"): Either<EventsData, ApiResponseError> =
//            apiService.getEvents(userProfile, appName, afterId, limit, markAsViewed, freshOnly, types)
//
//    /**mark certain events as unfresh, eg returning 'fresh' property as false
//     * method requires authorization
//     * @param ids list of userId's to set as read
//     * @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
//     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
//     * */
//    fun markEventsAsNotFresh(ids: List<String>, appName: String = "gls"): Either<ResultOk, ApiResponseError> = apiService.markEventsAsRead(ids, appName)
//
//    /**mark certain all events history of authorized user as not fresh
//     * method requires authorization
//     * @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
//     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
//     * */
//    fun markAllEventsAsNotFresh(appName: String = "gls"): Either<ResultOk, ApiResponseError> = apiService.markAllEventsAsRead(appName)
//
//    /**method for retreving count of fresh events of authorized user.
//     * method requires authorization
//     * @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
//     * @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
//     * */
//
//    fun getFreshNotificationCount(profileId: String, appName: String = "gls"): Either<FreshResult, ApiResponseError> = apiService.getUnreadCount(profileId, appName)
//
//    /**method returns current state of user registration process, user gets identified by [user] or
//     * by [phone]
//     *  @param user name of user, which registration state get fetched.
//     *  @param phone  of user, which registration state get fetched.
//     *  @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
//     *  @return [Either.Success] if transaction succeeded, otherwise [Either.Failure]
//     * */

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
     *  @param posting public posting key of user
     *  @param memo public memo key of user
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

//    fun isUserAuthed(): Either<Boolean, java.lang.Exception> {
//        return try {
//            val resp = markEventsAsNotFresh(emptyList())
//            Either.Success(resp is Either.Success)
//        } catch (e: java.lang.Exception) {
//            Either.Failure(e)
//        }
//    }


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

    /** transfer [amount] of [currency] [from] user [to] user
     * @param key active key [from] user
     * @param from user from wallet you want to transfer
     * @param to recipient of money
     * @param amount amount of [currency] to transfer. Must have 3 points precision, like 12.000 or 0.001
     * @param currency currency to transfer. GLS, for example
     * @param memo some additional info, that added to transfer
     * @throws SocketTimeoutException if socket was unable to answer in [Commun4jConfig.readTimeoutInSeconds] seconds
     */
    @JvmOverloads
    fun transfer(
            to: CyberName,
            amount: String,
            currency: String,
            memo: String = "",
            bandWidthRequest: BandWidthRequest? = null,
            key: String = keyStorage.getActiveAccountKeys().find { it.first == AuthType.ACTIVE }!!.second,
            from: CyberName = keyStorage.getActiveAccount()
    ): Either<TransactionCommitted<TransferCyberTokenStruct>, GolosEosError> {

        if (!amount.matches("([0-9]+\\.[0-9]{3})".toRegex())) throw IllegalArgumentException("wrong currency format. Must have 3 points precision, like 12.000 or 0.001")

        val callable = Callable {
            pushTransaction<TransferCyberTokenStruct>(TransferCyberTokenAction(
                    TransferCyberTokenStruct(
                            from, to,
                            CyberAsset("$amount $currency"), memo
                    )
            ).toActionAbi(
                    listOf(TransactionAuthorizationAbi(from.name, "active"))
            ), key, bandWidthRequest, null)
        }
        return callTilTimeoutExceptionVanishes(callable)
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

private fun createProvideBw(forUser: CyberName, actor: CyberName): ActionAbi = ActionAbi("cyber",
        "providebw",
        listOf(TransactionAuthorizationAbi(actor.name, "providebw")),
        AbiBinaryGenTransactionWriter(CompressionType.NONE)
                .squishProvideBandwichAbi(
                        ProvideBandwichAbi(
                                actor,
                                forUser)
                        , false).toHex())