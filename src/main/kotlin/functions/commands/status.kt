package com.poicraft.bot.v4.plugin.functions.commands

import com.poicraft.bot.v4.plugin.provider.command.B
import com.poicraft.bot.v4.plugin.provider.command.Command
import com.poicraft.bot.v4.plugin.provider.command.by
import com.poicraft.bot.v4.plugin.provider.command.command
import com.poicraft.bot.v4.plugin.provider.command.intro
import com.poicraft.bot.v4.plugin.provider.command.run
import com.poicraft.bot.v4.plugin.utils.*
import oshi.SystemInfo
import java.text.DecimalFormat

/**
 * 服务器状态
 */
@Command
fun B.status() {

    /**
     * 服务器硬件状态
     *
     * 基于 oshi
     */
    command("服务器状态") by "status" intro "获取服务器信息" run { event, _ ->

        val si = SystemInfo()

        event.subject.sendMessage(
            """
        CPU总体占用率: ${DecimalFormat("#.##%").format(getCPUUsage(si))}
        内存总体占用率: ${DecimalFormat("#.##%").format(getMemoryUsage(si))}
        内存占用情况: ${getMemoryUsageString(si)}
        磁盘占用情况: 
        ${getFileSystemUsageString(si)}
        """.replace("        ", "").trimIndent()
        )

    }

}