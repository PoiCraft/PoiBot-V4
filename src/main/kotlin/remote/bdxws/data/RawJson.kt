package com.poicraft.bot.v4.plugin.remote.bdxws.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val rawJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

/**
 * 远程的原JSON
 * https://github.com/WangYneos/BDXWebSocket
 * @author topjohncian
 */
@Serializable
class RawJson(
    val params: RawJsonParams,
    val type: String = "encrypted"
)

@Serializable
class RawJsonParams(
    val raw: String,
    val mode: String = "aes_cbc_pck7padding"
)
