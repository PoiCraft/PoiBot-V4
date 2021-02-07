package com.poicraft.bot.v4.plugin.database

import org.ktorm.database.Database

object DatabaseManager {
    private lateinit var databaseInstance: Database

    fun init() {
        databaseInstance = Database.connect("jdbc:sqlite:${System.getenv("HOMEPATH")}\\poi.sqlite")
    }

    fun instance() = databaseInstance
}