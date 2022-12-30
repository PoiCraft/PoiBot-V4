@file:Suppress("unused", "KotlinConstantConditions")

package com.poicraft.bot.v4.plugin.database

import com.poicraft.bot.v4.plugin.PluginMain
import com.poicraft.bot.v4.plugin.data.constants.UserStatus
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

/**
 * 该用户状态是否为验证状态
 * @sample genUserCondition
 */
val userIsValidateCondition = Users.status eq UserStatus.VERIFIED.ordinal

fun genUserCondition(expr: BinaryExpression<Boolean>, requireVerified: Boolean): BinaryExpression<Boolean> {
    return if (requireVerified) {
        (expr and userIsValidateCondition)
    } else {
        expr
    }
}

/**
 * 获取用户
 * @sample getUsersByXboxId
 * @sample getUsersByQQNumber
 */
fun getUsers(condition: (Query) -> Query): List<User>? {
    val result = PluginMain.database
        .from(Users)
        .select()
        .let(condition)
        .map { row -> Users.createEntity(row) }
    return result.ifEmpty { null }
}

/**
 * 用 QQ 号获取用户
 * @see getUsersByXboxId
 */
fun getUsersByQQNumber(qqNumber: Long, requireVerified: Boolean): List<User>? {
    return getUsers { query ->
        query.where { genUserCondition(Users.qqNumber eq qqNumber, requireVerified) }
    }
}

/**
 * 用 XboxID 获取用户
 * @see getUsersByQQNumber
 */
fun getUsersByXboxId(xboxId: String, requireVerified: Boolean): List<User>? {
    return getUsers { query ->
        query.where { genUserCondition(Users.xboxId eq xboxId, requireVerified) }
    }
}

/**
 * 获取 XboxID
 *
 * 1. 如果消息内含 At 信息 则会返回被 At 人数据库中绑定的 XboxID
 *
 * 2. 如果不含 At 信息:
 *
 *     1. requireVerified == true 时
 *      数据库内有 defaultId 的绑定信息则会返回 defaultId
 *      否则会返回 null
 *     2. requireVerified == false 时
 *      返回 defaultId
 * @param defaultId 不含 At 信息时应为 XboxId
 * @param requireVerified 用户状态是否需要为验证
 *
 * @see GroupMessageEvent.getQQNumber
 */
fun GroupMessageEvent.getXboxID(defaultId: String, requireVerified: Boolean = true): String? {
    val at: At? by this.message.orNull()
    return if (at == null) {
        if (requireVerified) {
            if (getUsersByXboxId(defaultId, requireVerified) != null) defaultId
            else null
        } else defaultId
    } else {
        val targets = getUsersByQQNumber(at!!.target, requireVerified)
        if (targets == null) null
        else targets[0].xboxId
    }
}

/**
 * 获取 QQ号
 *
 * 1. 如果消息内含 At 信息
 *
 *     1. requireVerified == true 时
 *      数据库内有被 At 人的绑定信息则会返回被 At 人的 QQ号
 *      否则会返回 null
 *     2. requireVerified == false 时
 *      返回被 At 人的 QQ号
 *
 * 2. 如果不含 At 信息 则会返回 defaultId 中绑定的QQ号
 *
 * @param defaultId 不含 At 信息时应为 XboxId
 * @param requireVerified 用户状态是否需要为验证
 *
 * @see GroupMessageEvent.getXboxID
 */
fun GroupMessageEvent.getQQNumber(defaultId: String, requireVerified: Boolean = true): Long? {
    val at: At? by this.message.orNull()
    return if (at != null) {
        if (requireVerified) {
            if (getUsersByQQNumber(at!!.target, requireVerified) != null) at!!.target
            else null
        } else at!!.target
    } else {
        val targets = getUsersByXboxId(defaultId, requireVerified)
        if (targets == null) null
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

/**
 * 用户是否为验证状态
 * @param defaultId XboxID
 */
fun ifVerified(defaultId: String): Boolean {
    val users = getUsersByXboxId(defaultId, false)
    return if (users != null) {
        when (users[0].status) {
            UserStatus.NOT_VERIFIED.ordinal -> false
            UserStatus.VERIFIED.ordinal -> true
            else -> false
        }
    } else {
        false
    }
}