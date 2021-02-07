package com.poicraft.bot.v4.plugin.database

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

@Suppress("PropertyName", "unused")
interface User: Entity<User> {
    var XboxID: String
    var QQNumber: String
    var status: Int
    var create_time: String
}

object Users: Table<User>("poi_users"){
    val create_time = varchar("create_time").bindTo { it.create_time }
    val status = int("status").bindTo { it.status }
    val XboxID = varchar("xbox_id").bindTo { it.XboxID }
    val QQNumber = varchar("qq_number").bindTo { it.QQNumber }
}