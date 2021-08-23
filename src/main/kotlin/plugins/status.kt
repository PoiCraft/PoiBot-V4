package com.poicraft.bot.v4.plugin.plugins

import com.poicraft.bot.v4.plugin.dsl.*
import com.poicraft.bot.v4.plugin.utils.formatByte
import oshi.SystemInfo
import oshi.hardware.CentralProcessor
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

/**
 * 服务器状态
 */
fun B.status() {

    command("服务器状态") by "status" intro "获取服务器信息" run { event, _ ->
        val sysInfo = SystemInfo()

        // 硬件信息
        val hal = sysInfo.hardware
        val processor = hal.processor
        val memory = hal.memory

        val prevTicks: LongArray = processor.systemCpuLoadTicks
        TimeUnit.SECONDS.sleep(1)
        val ticks: LongArray = processor.systemCpuLoadTicks
        val nice = ticks[CentralProcessor.TickType.NICE.index] - prevTicks[CentralProcessor.TickType.NICE.index]
        val irq = ticks[CentralProcessor.TickType.IRQ.index] - prevTicks[CentralProcessor.TickType.IRQ.index]
        val soft = ticks[CentralProcessor.TickType.SOFTIRQ.index] - prevTicks[CentralProcessor.TickType.SOFTIRQ.index]
        val steal = ticks[CentralProcessor.TickType.STEAL.index] - prevTicks[CentralProcessor.TickType.STEAL.index]
        val cSys = ticks[CentralProcessor.TickType.SYSTEM.index] - prevTicks[CentralProcessor.TickType.SYSTEM.index]
        val user = ticks[CentralProcessor.TickType.USER.index] - prevTicks[CentralProcessor.TickType.USER.index]
        val ioWait = ticks[CentralProcessor.TickType.IOWAIT.index] - prevTicks[CentralProcessor.TickType.IOWAIT.index]
        val idle = ticks[CentralProcessor.TickType.IDLE.index] - prevTicks[CentralProcessor.TickType.IDLE.index]
        val totalCpu = user + nice + cSys + idle + ioWait + irq + soft + steal

        //总内存
        val totalByte = memory.total
        //剩余
        val availableByte = memory.available

        event.subject.sendMessage(
            """
        CPU总体占用率: ${DecimalFormat("#.##%").format(1.0 - (idle * 1.0 / totalCpu))}
        内存总体占用率: ${DecimalFormat("#.##%").format((totalByte - availableByte) * 1.0 / totalByte)}
        内存占用情况: ${formatByte(totalByte - availableByte)}/${formatByte(totalByte)}
        """.trimIndent()
        )

    }

}