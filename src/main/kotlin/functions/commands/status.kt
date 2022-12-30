package com.poicraft.bot.v4.plugin.functions.commands

import com.poicraft.bot.v4.plugin.data.constants.Permission
import com.poicraft.bot.v4.plugin.provider.command.*
import com.poicraft.bot.v4.plugin.utils.oshi.*
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
        """.trimIndent()
        )

    }

    command("服务器状态-管理员") by "status-p" intro "获取服务器信息" require Permission.ADMIN_GROUP run { event, _ ->

        val si = SystemInfo()

        event.subject.sendMessage(
            """
        Full CPU:
        ${getFullCPUUsage(si)}
        
        Memory: ${getMemoryUsageString(si)}  ${DecimalFormat("#.##%").format(getMemoryUsage(si))}
        Swap: ${getSwapUsageString(si)}  ${DecimalFormat("#.##%").format(getSwapUsage(si))}
        
        Disk: 
        ${getFileSystemUsageString(si)}
        
        Processes:
        ${getProcessesInfoString(si)}
        """.replace("        ", "").trimIndent()
        )

    }

}