package com.poicraft.bot.v4.plugin

import com.poicraft.bot.v4.plugin.utils.Permission
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.contact.isOwner
import net.mamoe.mirai.event.events.GroupMessageEvent

abstract class Command {
    abstract val name: String
    abstract val aliases: List<String>
    open val introduction: String = ""

    open val permissionLevel = Permission.PERMISSION_LEVEL_EVERYONE

    abstract suspend fun handleMessage(event: GroupMessageEvent, args: List<String>)

    open suspend fun onPermissionDenied(permissionLevel: Permission, event: GroupMessageEvent, args: List<String>) {}

    suspend fun onMessage(event: GroupMessageEvent, args: List<String>) {
        when (permissionLevel) {
            Permission.PERMISSION_LEVEL_EVERYONE ->
                handleMessage(event, args)
            Permission.PERMISSION_LEVEL_ADMIN ->
                if (event.sender.isOperator()) handleMessage(
                    event,
                    args
                ) else onPermissionDenied(Permission.PERMISSION_LEVEL_ADMIN, event, args)
            Permission.PERMISSION_LEVEL_OWNER ->
                if (event.sender.isOwner()) handleMessage(
                    event,
                    args
                ) else onPermissionDenied(Permission.PERMISSION_LEVEL_OWNER, event, args)
        }
    }
}