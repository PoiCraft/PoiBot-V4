package com.poicraft.bot.v4.plugin.functions

import com.poicraft.bot.v4.plugin.constants.WhitelistStatus
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import kotlinx.coroutines.ExperimentalCoroutinesApi

class Whitelist {
    companion object {
        @ExperimentalCoroutinesApi
        suspend fun add(target: String): Pair<WhitelistStatus?, String> {
            return when (val result = BDXWSControl.runCmd("whitelist add \"$target\"")) {
                "Player added to whitelist" -> Pair(WhitelistStatus.PLAY_ADDED, result)
                "Player already in whitelist" -> Pair(WhitelistStatus.PLAYER_ALREADY_IN_WHITELIST, result)
                else -> Pair(null, result)
            }
        }

        @ExperimentalCoroutinesApi
        suspend fun remove(target: String): Pair<WhitelistStatus?, String> {
            return when (val result = BDXWSControl.runCmd("whitelist remove \"$target\"")) {
                "Player removed from whitelist" -> Pair(WhitelistStatus.PLAY_REMOVED, result)
                "Player not in whitelist" -> Pair(WhitelistStatus.PLAYER_NOT_IN_WHITELIST, result)
                else -> Pair(null, result)
            }
        }
    }
}