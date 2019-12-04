package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName

//
//getCommunityBlacklist:           // Получить список пользователей, заблокированных в сообществе
//communityId <string>         // Id сообщества
//communityAlias <string>      // Алиас сообщества
//offset <number>              // Сдвиг пагинации
//limit <number>               // Количество элементов
@JsonClass(generateAdapter = true)
internal data class CommunityBlacklistRequest(val communityId: String?,
                                     val communityAlias: String?,
                                     val offset: Int?,
                                     val limit: Int?)
//
//{
//    "userId": "tst5fsodxphz",
//    "username": "metz-milford-jr",
//    "avatarUrl": "https://i.pravatar.cc/300?u=57f742f942c3330c8c4395bbdc57dacebdcb5721"
//},
@JsonClass(generateAdapter = true)
data class CommunityBlacklistItem(val userId:CyberName, val username: String?, val avatarUrl: String?)

@JsonClass(generateAdapter = true)
data class CommunityBlacklistResponse(val items: List<CommunityBlacklistItem>): List<CommunityBlacklistItem> by items