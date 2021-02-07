package com.poicraft.bot.v4.plugin

import com.poicraft.bot.v4.plugin.utils.Permission
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.contact.isOwner
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import org.ktorm.database.Database

/**
 * 所有命令的父类
 * */
abstract class Command {
    /**
     * 命令的人类友好名称
     */
    abstract val name: String

    /**
     * 命令的程序友好名称
     */
    abstract val aliases: List<String>

    lateinit var db: Database
    fun loadCommand(database: Database){
        db = database
    }

    /**
     * 命令的简介 (可选)
     */
    open val introduction: String = ""

    /**
     * 命令的所需的权限等级, 默认为 任意群成员
     * @see Permission
     */
    open val permissionLevel = Permission.PERMISSION_LEVEL_EVERYONE

    /**
     * 命令所执行的事件
     */
    abstract suspend fun handleMessage(event: GroupMessageEvent, args: List<String>)

    /**
     * 权限等级不足时执行的事件 (可选) 默认为空
     */
    open suspend fun onPermissionDenied(permissionLevel: Permission, event: GroupMessageEvent, args: List<String>) {}

    open val argsRequired: Int = 0

    open suspend fun onArgsMissing(argsRequired: Int, event: GroupMessageEvent, args: List<String>){
        event.subject.sendMessage(
            event.source.quote() + """
            提供的参数(${args.size - 1}个)数量异常,需要${argsRequired}个
            """.trimIndent())
    }

    /**
     * 鉴权
     */
    suspend fun onMessage(event: GroupMessageEvent, args: List<String>) {
        if ((args.size  - 1) != argsRequired){
            onArgsMissing(argsRequired, event, args)
            return
        }
        when (permissionLevel) {
            Permission.PERMISSION_LEVEL_EVERYONE ->
                handleMessage(event, args)

            Permission.PERMISSION_LEVEL_ADMIN ->
                if (event.sender.isOperator())
                    handleMessage(
                        event,
                        args
                    )
                else
                    onPermissionDenied(Permission.PERMISSION_LEVEL_ADMIN, event, args)

            Permission.PERMISSION_LEVEL_OWNER ->
                if (event.sender.isOwner())
                    handleMessage(
                        event,
                        args
                    )
                else
                    onPermissionDenied(Permission.PERMISSION_LEVEL_OWNER, event, args)
        }
    }
}