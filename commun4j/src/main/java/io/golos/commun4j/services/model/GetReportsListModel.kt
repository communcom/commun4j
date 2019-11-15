package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.model.CyberDiscussion
import io.golos.commun4j.model.CyberDiscussionRaw

//getReportsList:                    // Списка контента, на который есть репорты
//communityIds <[string]/null>   // Массив communityId сообществ, если null то все сообщества где человек лидер
//status <string>('open')        // Фильтр по статусу рассмотрения
//[
//open
//| closed
//]
//contentType <string>           // Тип контента
//[
//post
//| comment
//]
//sortBy <string>('time')        // Сортировка
//[
//time                     // Сортировка по времени (от старых к новым)
//| timeDesc                 // Обратная сортировка по времени (от новых к старых)
//| reportsCount             // Сортировка по количеству репортов (от бОльшего к меньшему)
//]
//limit <number>(10)             // Ограничение на размер найденных результатов
//offset <number>(0)             // Количество результатов, которое надо "пропустить"

@JsonClass(generateAdapter = true)
internal data class GetReportsRequest(val communityIds: List<String>?,
                                      val status: String?,
                                      val contentType: String?,
                                      val sortBy: String?,
                                      val limit: Int?,
                                      val offset: Int?)

@JsonClass(generateAdapter = true)
data class GetReportsResponse(val items: List<CyberDiscussion>)

@JsonClass(generateAdapter = true)
data class GetReportsResponseRaw(val items: List<CyberDiscussionRaw>)

enum class ReportsRequestStatus {
    OPEN, CLOSED;

    override fun toString(): String {
        return when (this) {
            OPEN -> "open"
            CLOSED -> "closed"
        }
    }
}

enum class ReportRequestContentType {
    POST, COMMENT;

    override fun toString(): String {
        return when (this) {
            POST -> "post"
            COMMENT -> "comment"
        }
    }
}

enum class ReportsRequestTimeSort {
    TIME, TIME_DESC, REPORTS_COUNT;

    override fun toString(): String {
        return when (this) {
            TIME -> "time"
            TIME_DESC -> "timeDesc"
            REPORTS_COUNT -> "reportsCount"
        }
    }
}