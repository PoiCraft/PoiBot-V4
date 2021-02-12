package com.poicraft.bot.v4.plugin.remote

/**
 * 远程控制类的父类
 * @author topjohncian
 */
abstract class Control {
    /**
     * 初始化 Control
     */
    abstract fun init()

    /**
     * 执行命令(不需要获得返回值)
     */
    abstract suspend fun runCmdNoRes(cmdString: String)

    /**
     * 执行命令
     */
    abstract suspend fun runCmd(cmdString: String): String
}