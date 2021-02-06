package com.poicraft.bot.v4.plugin.remote

/**
 * 远程控制类的父类
 * @author topjohncian
 */
abstract class Control {
    /**
     * 执行命令(不需要获得返回值)
     */
    abstract fun runCmdNoRes(cmdString: String)

    /**
     * 执行命令
     */
    abstract fun runCmd(cmdString: String): String
}