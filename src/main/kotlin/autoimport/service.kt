package com.poicraft.bot.v4.plugin.autoimport

import com.poicraft.bot.v4.plugin.PluginMain
import com.poicraft.bot.v4.plugin.services.Service
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
fun PluginMain.loadAllService() {
    getAllAnnotatedWith(Service::class)
        .forEach { it.call(this) }
}