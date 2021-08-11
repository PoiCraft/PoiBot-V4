package com.poicraft.bot.v4.plugin.functions

import com.poicraft.bot.v4.plugin.constants.WhitelistStatus
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import kotlinx.coroutines.ExperimentalCoroutinesApi

class Whitelist {
    companion object {
        @ExperimentalCoroutinesApi
        suspend fun add(target: String): WhitelistStatus? {
            return when (val result = BDXWSControl.runCmd("whitelist add \"$target\"")) {
                "Player added to whitelist" -> WhitelistStatus.PLAY_ADDED
                "Player already in whitelist" -> WhitelistStatus.PLAYER_ALREADY_IN_WHITELIST
                else -> null
            }
        }

        @ExperimentalCoroutinesApi
        suspend fun remove(target: String): WhitelistStatus? {
            return when (val result = BDXWSControl.runCmd("whitelist remove \"$target\"")) {
                "Player removed from whitelist" -> WhitelistStatus.PLAY_REMOVED
                "Player not in whitelist" -> WhitelistStatus.PLAYER_NOT_IN_WHITELIST
                else -> null
            }
        }
    }
}