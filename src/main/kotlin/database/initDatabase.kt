package com.poicraft.bot.v4.plugin.database

import com.poicraft.bot.v4.plugin.PluginData
import com.poicraft.bot.v4.plugin.PluginMain
import com.poicraft.bot.v4.plugin.utils.KtormLoggerAdapter
import org.ktorm.database.Database
import org.ktorm.logging.LogLevel

fun PluginMain.initDatabase() {
    database = Database.connect(
        "jdbc:sqlite:${PluginData.databasePath}",
        logger = KtormLoggerAdapter(LogLevel.DEBUG, PluginMain.logger)
    )
}