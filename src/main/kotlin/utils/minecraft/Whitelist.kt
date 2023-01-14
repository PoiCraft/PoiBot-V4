package com.poicraft.bot.v4.plugin.utils.minecraft

import com.poicraft.bot.v4.plugin.data.constants.WhitelistStatus
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * 白名单管理
 */
class Whitelist {
    companion object {
        /**
         * 添加白名单
         * @param target 玩家的 Xbox ID
         */
        @ExperimentalCoroutinesApi
        suspend fun add(target: String): Pair<WhitelistStatus?, String> {
            return when (val result = BDXWSControl.runCmd("whitelist add \"$target\"")) {
                "Player added to whitelist", "Player added to allowlist" -> Pair(WhitelistStatus.PLAY_ADDED, result)
                "Player already in whitelist", "Player already in allowlist" -> Pair(
                    WhitelistStatus.PLAYER_ALREADY_IN_WHITELIST,
                    result
                )

                else -> Pair(null, result)
            }
        }

        /**
         * 移除白名单
         * @param target 玩家的 Xbox ID
         */
        @ExperimentalCoroutinesApi
        suspend fun remove(target: String): Pair<WhitelistStatus?, String> {
            return when (val result = BDXWSControl.runCmd("whitelist remove \"$target\"")) {
                "Player removed from whitelist", "Player removed from allowlist" -> Pair(
                    WhitelistStatus.PLAY_REMOVED,
                    result
                )

                "Player not in whitelist", "Player not in allowlist" -> Pair(
                    WhitelistStatus.PLAYER_NOT_IN_WHITELIST,
                    result
                )

                else -> Pair(null, result)
            }
        }
    }
}