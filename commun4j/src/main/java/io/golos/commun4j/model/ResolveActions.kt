package io.golos.commun4j.model

import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.AbiBinaryGenTransactionWriter
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.chain.actions.transaction.misc.ProvideBandwichAbi
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.utils.toCyberName

fun resolveActions(actions: List<ActionAbi>,
                   bandWidthRequest: BandWidthRequest?,
                   clientAuthRequest: ClientAuthRequest?): List<ActionAbi> {
    return actions
            .map { action ->
                //client permission for cyber
                action
                        .takeIf { clientAuthRequest != null }
                        ?.let { action ->
                            action.copy(
                                    authorization = action.authorization + TransactionAuthorizationAbi(action.account, "clients")
                            )
                        } ?: action
            }.let { resultingActions ->
                resultingActions
                        .toSet()
                        .plus(actions //providebw for `c`
                                .takeIf { bandWidthRequest != null }
                                ?.run {
                                    map { action -> createProvideBw(action.account.toCyberName(), "c".toCyberName(), "c".toCyberName()) }  //provide bw for `c`
                                } ?: emptySet())
                        .plus(actions//provide bw for actor
                                .takeIf { bandWidthRequest != null }
                                ?.run {
                                    map { action -> createProvideBw(action.authorization.first().actor.toCyberName(), bandWidthRequest!!.actor) }  //provide bw for `c`
                                } ?: emptySet())
                        .toList()
            }
}


fun createProvideBw(forUser: CyberName, provider: CyberName, actor: CyberName = provider): ActionAbi = ActionAbi("cyber",
        "providebw",
        listOf(TransactionAuthorizationAbi(actor.name, "providebw")),
        AbiBinaryGenTransactionWriter(CompressionType.NONE)
                .squishProvideBandwichAbi(
                        ProvideBandwichAbi(
                                provider,
                                forUser)
                        , false).toHex())