package com.poicraft.bot.v4.plugin.database

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar

@Suppress("PropertyName")
interface User: Entity<User> {
    var XboxID: String
    var QQNumber: Long
    var Status: Int
    var CreateTime: String
}

object Users: Table<User>("poi_users") {
    val CreateTime = varchar("create_time").bindTo { it.CreateTime }
    val Status = int("status").bindTo { it.Status }
    val XboxID = varchar("xbox_id").bindTo { it.XboxID }
    val QQNumber = long("qq_number").bindTo { it.QQNumber }
}