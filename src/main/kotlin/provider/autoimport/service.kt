package com.poicraft.bot.v4.plugin.provider.autoimport

import com.poicraft.bot.v4.plugin.PluginMain
import com.poicraft.bot.v4.plugin.provider.service.Service
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
fun PluginMain.loadAllService() {
    getAllAnnotatedWith(Service::class)
        .forEach { it.call(this) }
}