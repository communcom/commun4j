package io.golos.commun4j.model

import com.squareup.moshi.JsonClass

//userId <string>                // Id пользователя
//communityId <string/null>      // Id сообщества
//communityAlias <string/null>   // Alias сообщества (замена communityId при необходимости)
//allowNsfw <boolean>(false)     // Разрешать выдачу NSFW-контента
//type <string>('community')     // Тип ленты
//[
//new                      // Лента актуальных постов
//| community                // Лента сообщества
//| subscriptions            // Лента пользователя по подпискам
//| byUser                   // Лента постов с авторством пользователя
//| topLikes                 // Лента топ постов по лайкам (up-down)
//| topComments              // Лента топ постов по количеству комментариев
//| topRewards               // Лента топ постов по размеру наград
//]
//sortBy <string>('time')        // Тип ленты
//[
//time                     // Сортировка по времени (от новых к старым)
//| timeDesc                 // Обратная сортировка по времени (от старых к новым)
//]
//timeframe <string>('day')      // Временные рамки (только для топ-постов)
//[
//day                      // День
//| week                     // Неделя
//| month                    // Месяц
//| all                      // Все время
//]
//limit <number>(10)             // Ограничение на размер найденных результатов
//offset <number>(0)


@JsonClass(generateAdapter = true)
data class GetPostsRequest(val userId: String?,
                           val communityId: String?,
                           val communityAlias: String?,
                           val allowNsfw: Boolean?,
                           val type: String?,
                           val sortBy: String?,
                           val timeframe: String?,
                           val limit: Int?,
                           val offset: Int?)

enum class FeedTimeFrame {
    DAY, WEEK, MONTH, ALL;

    override fun toString(): String {
        return when (this) {
            DAY -> "day"
            WEEK -> "week"
            MONTH -> "month"
            ALL -> "all"
        }
    }
}

enum class FeedSortByType {
    TIME, TIME_DESC;

    override fun toString(): String {

        return when (this) {
            TIME -> "time"
            TIME_DESC -> "timeDesc"
        }
    }
}

enum class FeedType {
    NEW, COMMUNITY, SUBSCRIPTION, BY_USER, TOP_LIKES, TOP_COMMENTS, TOP_REWARDS, HOT, VOTED;

    override fun toString(): String {
        return when (this) {
            NEW -> "new"
            COMMUNITY -> "community"
            SUBSCRIPTION -> "subscriptions"
            BY_USER -> "byUser"
            TOP_LIKES -> "topLikes"
            TOP_COMMENTS -> "topComments"
            TOP_REWARDS -> "topRewards"
            HOT -> "hot"
            VOTED -> "voted"
        }
    }
}