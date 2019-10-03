package io.golos.commun4j.model

import io.golos.commun4j.http.rpc.model.account.request.AccountName
import io.golos.commun4j.http.rpc.model.account.response.Account
import io.golos.commun4j.http.rpc.model.block.request.BlockNumOrId
import io.golos.commun4j.http.rpc.model.block.response.Block
import io.golos.commun4j.http.rpc.model.block.response.BlockHeaderState
import io.golos.commun4j.http.rpc.model.contract.request.*
import io.golos.commun4j.http.rpc.model.contract.response.*
import io.golos.commun4j.http.rpc.model.info.Info
import io.golos.commun4j.http.rpc.model.producer.request.GetProducers
import io.golos.commun4j.http.rpc.model.producer.response.ProducerList
import io.golos.commun4j.http.rpc.model.signing.GetRequiredKeysBody
import io.golos.commun4j.http.rpc.model.signing.PushTransaction
import io.golos.commun4j.http.rpc.model.signing.RequiredKeys
import io.reactivex.Single
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File

interface CyberWayChainApi {

    fun getInfo(): Single<Response<Info>>

    fun getProducers(body: GetProducers): Single<Response<ProducerList>>

    fun getBlock(body: BlockNumOrId): Single<Response<Block>>

    fun getBlockHeaderState(body: BlockNumOrId): Single<Response<BlockHeaderState>>

    fun getAccount(body: AccountName): Single<Response<Account>>

    fun getAbi(body: AccountName): Single<Response<AbiForAccount>>

    fun getCode(body: GetCodeByAccountName): Single<Response<CodeForAccount>>

    fun getRawCodeAndAbi(body: AccountName): Single<Response<RawCodeForAccount>>

    fun getTableRows(body: GetTableRows): Single<Response<ContractTableRows>>

    fun getCurrencyBalance(body: GetCurrencyBalance): Single<Response<List<String>>>

    fun abiJsonToBin(body: RequestBody): Single<Response<BinaryHex>>

    fun abiBinToJson(body: AbiBinToJson): Single<Response<ResponseBody>>

    fun getRequiredKeys(body: GetRequiredKeysBody): Single<Response<RequiredKeys>>

    fun getCurrencyStats(body: GetCurrencyStats): Single<Response<ResponseBody>>

    fun pushTransaction(body: PushTransaction): Single<Response<String>>

    fun resolveNames(body: List<String>): Single<List<ResolvedName>>

    fun uploadImage(file: File): Single<String>

    fun shutDown(): Any
}