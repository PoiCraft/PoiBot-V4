package com.poicraft.bot.v4.plugin.database

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

@Suppress("PropertyName", "unused")
interface User: Entity<User> {
    var XboxID: String
    var QQNumber: String
    var Status: Int
    var CreateTime: String
}

object Users: Table<User>("poi_users") {
    val CreateTime = varchar("create_time").bindTo { it.CreateTime }
    val Status = int("status").bindTo { it.Status }
    val XboxID = varchar("xbox_id").bindTo { it.XboxID }
    val QQNumber = varchar("qq_number").bindTo { it.QQNumber }
}