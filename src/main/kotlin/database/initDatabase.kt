package com.poicraft.bot.v4.plugin.database

import com.poicraft.bot.v4.plugin.PluginData
import com.poicraft.bot.v4.plugin.PluginMain
import com.poicraft.bot.v4.plugin.utils.KtormLoggerAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.ktorm.database.Database
import org.ktorm.logging.LogLevel

@ExperimentalCoroutinesApi
fun PluginMain.initDatabase() {
    database = Database.connect(
        url = "jdbc:sqlite:${PluginData.databasePath}",
        driver = "org.sqlite.JDBC",
        logger = KtormLoggerAdapter(LogLevel.DEBUG, PluginMain.logger)
    )
}