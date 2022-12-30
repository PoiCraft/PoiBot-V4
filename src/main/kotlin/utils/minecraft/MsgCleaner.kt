package com.poicraft.bot.v4.plugin.utils.minecraft

fun String.cleanMsg(): String {
    return this.replace("§[0-9a-fk-or]".toRegex(), "")
}