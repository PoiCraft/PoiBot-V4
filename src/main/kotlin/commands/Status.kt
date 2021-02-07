package com.poicraft.bot.v4.plugin.commands

import com.poicraft.bot.v4.plugin.Command
import net.mamoe.mirai.event.events.GroupMessageEvent
import oshi.SystemInfo
import java.util.concurrent.TimeUnit
import oshi.hardware.CentralProcessor
import java.text.DecimalFormat


@Suppress("BlockingMethodInNonBlockingContext")
object Status: Command() {
    override val name: String = "服务器状态"
    override val aliases: List<String> = listOf(
        "status"
    )

    override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
        val systemInfo = SystemInfo()
        val processor = systemInfo.hardware.processor
        val memory = systemInfo.hardware.memory

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

        event.subject.sendMessage("""
        CPU总体占用率: ${DecimalFormat("#.##%").format(1.0-(idle * 1.0 / totalCpu))}
        内存总体占用率: ${DecimalFormat("#.##%").format((totalByte-availableByte)*1.0/totalByte)}
        内存占用情况: ${formatByte(totalByte-availableByte)}/${formatByte(totalByte)}
        """.trimIndent())

    }

    private fun formatByte(byteNumber: Long): String? {
        val format = 1024.0
        val kbNumber = byteNumber / format
        if (kbNumber < format) {
            return DecimalFormat("#.##KB").format(kbNumber)
        }
        val mbNumber = kbNumber / format
        if (mbNumber < format) {
            return DecimalFormat("#.##MB").format(mbNumber)
        }
        val gbNumber = mbNumber / format
        if (gbNumber < format) {
            return DecimalFormat("#.##GB").format(gbNumber)
        }
        val tbNumber = gbNumber / format
        return DecimalFormat("#.##TB").format(tbNumber)
    }
}