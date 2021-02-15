package com.poicraft.bot.v4.plugin.database

import com.poicraft.bot.v4.plugin.constants.UserStatus
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.orNull
import org.ktorm.dsl.*
import org.ktorm.entity.Entity
import org.ktorm.expression.BinaryExpression
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

val userIsValidateCondition = Users.status eq UserStatus.VERIFIED.ordinal

fun genUserCondition(expr: BinaryExpression<Boolean>, requireVerified: Boolean): BinaryExpression<Boolean> {
    return if (requireVerified) {
        (expr and userIsValidateCondition)
    } else {
        expr
    }
}

fun getUsers(condition: (Query) -> Query): List<User> {
    return DatabaseManager.instance()
        .from(Users)
        .select()
        .let(condition)
        .map { row -> Users.createEntity(row) }
}

fun getUsersByQQNumber(qqNumber: Long, requireVerified: Boolean): List<User> {
    return getUsers { query ->
        query.where { genUserCondition(Users.qqNumber eq qqNumber, requireVerified) }
    }
}

fun getUsersByXboxId(xboxId: String, requireVerified: Boolean): List<User> {
    return getUsers { query ->
        query.where { genUserCondition(Users.xboxId eq xboxId, requireVerified) }
    }
}

fun GroupMessageEvent.getXboxID(defaultId: String, requireVerified: Boolean = true): String? {
    val at: At? by this.message.orNull()
    return if (at == null) {
        if (requireVerified) {
            if (getUsersByXboxId(defaultId, requireVerified).isNotEmpty()) defaultId
            else defaultId
        } else defaultId
    } else {
        val targets = getUsersByQQNumber(at!!.target, requireVerified)
        if (targets.isEmpty()) null
        else targets[0].xboxId
    }
}

fun GroupMessageEvent.getQQNumber(defaultId: String, requireVerified: Boolean = true): Long? {
    val at: At? by this.message.orNull()
    return if (at != null) {
        if (requireVerified) {
            if (getUsersByQQNumber(at!!.target, requireVerified).isNotEmpty()) at!!.target
            else at!!.target
        } else at!!.target
    } else {
        val targets = getUsersByXboxId(defaultId, requireVerified)
        if (targets.isEmpty()) null
        else targets[0].qqNumber
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

fun ifVerified(defaultId: String): Boolean {
    return when (getUsersByXboxId(defaultId, false)[0].status) {
        UserStatus.NOT_VERIFIED.ordinal -> false
        UserStatus.VERIFIED.ordinal -> true
        else -> false
    }
}