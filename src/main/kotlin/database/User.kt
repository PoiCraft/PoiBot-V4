package com.poicraft.bot.v4.plugin.database

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import org.ktorm.dsl.*
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar

@Suppress("PropertyName")
interface User : Entity<User> {
    var XboxID: String
    var QQNumber: Long
    var Status: Int
    var CreateTime: String
}

object Users : Table<User>("poi_users") {
    val CreateTime = varchar("create_time").bindTo { it.CreateTime }
    val Status = int("status").bindTo { it.Status }
    val XboxID = varchar("xbox_id").bindTo { it.XboxID }
    val QQNumber = long("qq_number").bindTo { it.QQNumber }
}

fun GroupMessageEvent.getXboxID(default_id: String): String? {
    val at: At? = this.message.filterIsInstance<At>().firstOrNull()
    return if (at == null) {
        default_id
    } else {
        val targets = DatabaseManager.instance().from(Users).select(Users.QQNumber, Users.XboxID)
            .where { Users.QQNumber eq at.target }.map { it.getString(2) }
        if (targets.isEmpty()) null
        else targets[0]
    }
}

fun GroupMessageEvent.getQQNumber(default_id: String): Long? {
    val at: At? = this.message.filterIsInstance<At>().firstOrNull()
    return if (at != null) {
        at.target
    } else {
        val targets = DatabaseManager.instance().from(Users).select(Users.QQNumber, Users.XboxID)
            .where { Users.XboxID eq default_id }.map { it.getLong(1) }
        if (targets.isEmpty()) null
        else targets[0]
    }
}