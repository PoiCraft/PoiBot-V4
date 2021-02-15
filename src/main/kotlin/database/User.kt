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
    var id: Int
    var xboxId: String
    var qqNumber: Long
    var status: Int
    var createTime: String
}

object Users : Table<User>("poi_users") {
    val id = int("id").primaryKey().bindTo { it.id }
    val createTime = varchar("create_time").bindTo { it.createTime }
    val status = int("status").bindTo { it.status }
    val xboxId = varchar("xbox_id").bindTo { it.xboxId }
    val qqNumber = long("qq_number").bindTo { it.qqNumber }
}

fun GroupMessageEvent.getXboxID(default_id: String, require_verified: Boolean = true): String? {
    val at: At? by this.message.orNull()
    return if (at == null) {
        if (require_verified) {
            if (DatabaseManager.instance().from(Users).select(Users.xboxId, Users.status)
                    .where { Users.xboxId eq default_id and (Users.status eq UserStatus.VERIFIED.ordinal) }.totalRecords == 0
            ) null
            else default_id
        } else default_id
    } else {
        val targets = DatabaseManager.instance().from(Users).select(Users.qqNumber, Users.xboxId, Users.status)
            .let {
                if (require_verified)
                    it.where { Users.qqNumber eq at!!.target and (Users.status eq UserStatus.VERIFIED.ordinal) }
                else
                    it.where { Users.qqNumber eq at!!.target }
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
            if (DatabaseManager.instance().from(Users).select(Users.qqNumber, Users.status)
                    .where { Users.qqNumber eq at!!.target and (Users.status eq UserStatus.VERIFIED.ordinal) }.totalRecords == 0
            ) null
            else at!!.target
        } else at!!.target
    } else {
        val targets = DatabaseManager.instance().from(Users).select(Users.qqNumber, Users.xboxId, Users.status)
            .let {
                if (require_verified) {
                    it.where { Users.xboxId eq default_id and (Users.status eq UserStatus.VERIFIED.ordinal) }
                } else {
                    it.where { Users.xboxId eq default_id }
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
        when (DatabaseManager.instance().from(Users).select(Users.xboxId, Users.status).where { Users.xboxId eq target }
            .map { it.getInt(2) }.getOrNull(0)) {
            null -> false
            UserStatus.NOT_VERIFIED.ordinal -> false
            UserStatus.VERIFIED.ordinal -> true
            else -> false
        }
    }
}