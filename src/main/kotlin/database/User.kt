package com.poicraft.bot.v4.plugin.database

import com.poicraft.bot.v4.plugin.constants.UserStatus
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.orNull
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

fun GroupMessageEvent.getXboxID(default_id: String, require_verified: Boolean = true): String? {
    val at: At? by this.message.orNull()
    return if (at == null) {
        if (require_verified) {
            if (DatabaseManager.instance().from(Users).select(Users.XboxID, Users.Status)
                    .where { Users.XboxID eq default_id and (Users.Status eq UserStatus.VERIFIED.ordinal) }.totalRecords == 0
            ) null
            else default_id
        } else default_id
    } else {
        val targets = DatabaseManager.instance().from(Users).select(Users.QQNumber, Users.XboxID, Users.Status)
            .let {
                if (require_verified)
                    it.where { Users.QQNumber eq at!!.target and (Users.Status eq UserStatus.VERIFIED.ordinal) }
                else
                    it.where { Users.QQNumber eq at!!.target }
            }
            .map { it.getString(2) }
        if (targets.isEmpty()) null
        else targets[0]
    }
}

fun GroupMessageEvent.getQQNumber(default_id: String, require_verified: Boolean = true): Long? {
    val at: At? by this.message.orNull()
    return if (at != null) {
        if (require_verified) {
            if (DatabaseManager.instance().from(Users).select(Users.QQNumber, Users.Status)
                    .where { Users.QQNumber eq at!!.target and (Users.Status eq UserStatus.VERIFIED.ordinal) }.totalRecords == 0
            ) null
            else at!!.target
        } else at!!.target
    } else {
        val targets = DatabaseManager.instance().from(Users).select(Users.QQNumber, Users.XboxID, Users.Status)
            .let {
                if (require_verified) {
                    it.where { Users.XboxID eq default_id and (Users.Status eq UserStatus.VERIFIED.ordinal) }
                } else {
                    it.where { Users.XboxID eq default_id }
                }
            }
            .map { it.getLong(1) }
        if (targets.isEmpty()) null
        else targets[0]
    }
}

@ExperimentalCoroutinesApi
suspend fun GroupMessageEvent.ifOnline(default_id: String): Boolean? {
    val target = this.getXboxID(default_id, false) ?: return null
    val result = BDXWSControl.runCmd("testfor \"$target\"")
    return when {
        result.contains("No targets matched selector") -> false
        result.contains("Found") -> true
        else -> null
    }
}

fun GroupMessageEvent.ifVerified(default_id: String): Boolean {
    val target = this.getXboxID(default_id, false)
    return if (target == null) {
        false
    } else {
        when (DatabaseManager.instance().from(Users).select(Users.XboxID, Users.Status).where { Users.XboxID eq target }
            .map { it.getInt(2) }.getOrNull(0)) {
            null -> false
            UserStatus.NOT_VERIFIED.ordinal -> false
            UserStatus.VERIFIED.ordinal -> true
            else -> false
        }
    }
}