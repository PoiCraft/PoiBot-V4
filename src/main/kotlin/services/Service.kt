package com.poicraft.bot.v4.plugin.services

abstract class Service {
    abstract fun init()
}

object Services {
    private val services = listOf<Service>(
        ValidateUserService,
        LoggerServer
    )

    fun init() {
        services.forEach { it.init() }
    }
}