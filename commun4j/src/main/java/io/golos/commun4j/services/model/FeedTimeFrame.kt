package io.golos.commun4j.services.model

enum class FeedTimeFrame {
    DAY, WEEK, MONTH, YEAR, ALL, WILSON_HOT, WILSON_TRENDING;

    override fun toString(): String {
        return when (this){
            DAY -> "day"
            WEEK -> "week"
            MONTH -> "month"
            YEAR -> "year"
            ALL -> "all"
            WILSON_HOT -> "WilsonHot"
            WILSON_TRENDING -> "WilsonTrending"
        }
    }
}