package com.poicraft.bot.v4.plugin.functions.services

import com.poicraft.bot.v4.plugin.PluginData
import com.poicraft.bot.v4.plugin.PluginMain
import com.poicraft.bot.v4.plugin.provider.service.Service
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@ExperimentalCoroutinesApi
@Service
fun httpService(plugin: PluginMain) {
    plugin.launch {
        PluginData.httpConfig.apply {
            embeddedServer(
                Netty,
                port = port,
                host = "0.0.0.0",
                module = Application::module
            ).start()
        }
    }
}

@ExperimentalCoroutinesApi
fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }

    authentication {
        basic(name = "auth-basic") {
            realm = "PoiCraft Bot Auth"
            validate { credentials ->
                if (credentials.name == PluginData.httpConfig.credential && credentials.password == PluginData.httpConfig.password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }

    routing {
        alertManagerRoute()
    }
}