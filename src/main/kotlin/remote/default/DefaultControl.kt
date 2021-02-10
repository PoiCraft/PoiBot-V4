package com.poicraft.bot.v4.plugin.remote.default

import com.poicraft.bot.v4.plugin.remote.Control

class DefaultControl : Control() {
    /**
     * 执行命令(不需要获得返回值)
     */
    override fun runCmdNoRes(cmdString: String) {}

    /**
     * 执行命令
     */
    override fun runCmd(cmdString: String) = ""
}