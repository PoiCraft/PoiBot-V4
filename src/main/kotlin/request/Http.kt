package com.poicraft.bot.v4.plugin.request

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*

object Http {
    private var client: HttpClient? = null

    private fun create() {
        client = HttpClient(CIO) {
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