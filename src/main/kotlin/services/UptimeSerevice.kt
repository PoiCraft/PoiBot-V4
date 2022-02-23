package com.poicraft.bot.v4.plugin.services

import com.poicraft.bot.v4.plugin.PluginData
import com.poicraft.bot.v4.plugin.PluginMain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


@ExperimentalCoroutinesApi
@Service
fun uptimeService(plugin: PluginMain) {
    if (PluginData.uptimeConfig.url.isNotEmpty()) {
        val timer = Timer()
        timer.schedule(
            UptimeTasker,
            0,
            PluginData.uptimeConfig.pingInterval.toLong() * 1000
        )
    }
}

object UptimeTasker : TimerTask() {
    override fun run() {
        val url = URL(PluginData.uptimeConfig.url)
        val con = url.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        PluginMain.logger.info("Uptime: ${con.responseCode}")
    }
}