package com.poicraft.bot.v4.plugin.utils.minecraft

/**
 * 去除 Minecraft 消息中的格式化代码
 */
fun String.cleanMsg(): String {
    return this.replace("§[0-9a-fk-or]".toRegex(), "")
}