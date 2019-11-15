package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName

//getEntityReports:                  // Получение списка репортов конкретного контента
//userId <string>                // Id пользователя
//permlink <string>              // Пермлинк контента
//communityId <string>           // Идетификатор сообщества, в котором опубликован контент
//limit <number>(10)             // Ограничение на размер найденных результатов
//offset <number>(0)             // Количество результатов, которое надо "пропустить"

@JsonClass(generateAdapter = true)
internal data class GetEntityReportsRequest(val userId: CyberName?, val permlink: String?,
                                            val communityId: String?, val limit:Int?, val offset:Int?)