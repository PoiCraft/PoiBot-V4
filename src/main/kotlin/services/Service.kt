package com.poicraft.bot.v4.plugin.services

import com.poicraft.bot.v4.plugin.PluginMain
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
val services = listOf(
    ::validateUser,
    ::broadcastService
)


@ExperimentalCoroutinesApi
fun PluginMain.initService() = services.forEach { it(this) }