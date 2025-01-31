package com.michaelliu.twsestockinfo.utils

/**
 * 網路請求retry延遲策略
 * - [Exponential]：指數延遲 (delay *= 2)
 * - [Fixed]：固定延遲毫秒數
 * - [Custom]：自訂函式計算下一次延遲時間
 */
sealed class DelayStrategy {
    data object Exponential : DelayStrategy()

    data class Fixed(val delayMillis: Long) : DelayStrategy()

    data class Custom(val block: (attempt: Int, currentDelay: Long) -> Long) : DelayStrategy()
}