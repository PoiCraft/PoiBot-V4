package com.poicraft.bot.v4.plugin.services

import com.poicraft.bot.v4.plugin.PluginMain
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import com.poicraft.bot.v4.plugin.remote.bdxws.data.OnChatRes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.mamoe.mirai.utils.info

object ValidateUserService : Service() {
    @ExperimentalCoroutinesApi
    override fun init() {
        BDXWSControl.addEventListener(OnChatRes::class) {
            PluginMain.logger.info { this.params.text }
        }
    }
}