package com.poicraft.bot.v4.plugin.request

import com.poicraft.bot.v4.plugin.PluginMain
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*

object Http {
    private var client: HttpClient? = null

    private fun create() {
        client = HttpClient(CIO) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        PluginMain.logger.info(message)
                    }
                }
            }

            install(JsonFeature) {
                serializer = KotlinxSerializer(
                    kotlinx.serialization.json.Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }

    fun client(): HttpClient? {
        if (client === null) {
            create()
        }
        return client
    }
}