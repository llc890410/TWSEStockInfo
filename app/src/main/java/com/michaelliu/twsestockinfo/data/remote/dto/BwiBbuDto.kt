package com.michaelliu.twsestockinfo.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BwiBbuDto(
    @SerialName("Code") val code: String?,
    @SerialName("Name") val name: String?,
    @SerialName("PEratio") val peRatio: String?,
    @SerialName("DividendYield") val dividendYield: String?,
    @SerialName("PBratio") val pbRatio: String?
)