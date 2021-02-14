package com.poicraft.bot.v4.plugin.commands

import com.poicraft.bot.v4.plugin.Command
import net.mamoe.mirai.event.events.GroupMessageEvent

/**
 * 随机传送
 * @see Command
 * @author topjohncian
 */
object RandomTP : Command() {
    init {
        newSubCommand(PlayerRandomTP)
    }

    override val name: String = "随机传送"
    override val aliases: List<String> = listOf("rtp", "randomtp")
    override val introduction: String = "随机传送"
    override val enableSubCommand: Boolean = true

    /**
     * 玩家随机传送
     * @see Command
     * @author topjohncian
     */
    object PlayerRandomTP : Command() {
        override val name: String = "player"
        override val aliases: List<String> = listOf("player")

        override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {

        }
    }
}