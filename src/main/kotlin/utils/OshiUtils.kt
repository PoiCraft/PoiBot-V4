package com.poicraft.bot.v4.plugin.utils

import oshi.SystemInfo
import oshi.hardware.CentralProcessor
import java.util.concurrent.TimeUnit

fun getCPUUsage(si: SystemInfo): Double {
    val processors = si.hardware.processor
    val prevTicks: LongArray = processors.systemCpuLoadTicks
    TimeUnit.SECONDS.sleep(1)
    val ticks: LongArray = processors.systemCpuLoadTicks
    val nice = ticks[CentralProcessor.TickType.NICE.index] - prevTicks[CentralProcessor.TickType.NICE.index]
    val irq = ticks[CentralProcessor.TickType.IRQ.index] - prevTicks[CentralProcessor.TickType.IRQ.index]
    val soft = ticks[CentralProcessor.TickType.SOFTIRQ.index] - prevTicks[CentralProcessor.TickType.SOFTIRQ.index]
    val steal = ticks[CentralProcessor.TickType.STEAL.index] - prevTicks[CentralProcessor.TickType.STEAL.index]
    val cSys = ticks[CentralProcessor.TickType.SYSTEM.index] - prevTicks[CentralProcessor.TickType.SYSTEM.index]
    val user = ticks[CentralProcessor.TickType.USER.index] - prevTicks[CentralProcessor.TickType.USER.index]
    val ioWait = ticks[CentralProcessor.TickType.IOWAIT.index] - prevTicks[CentralProcessor.TickType.IOWAIT.index]
    val idle = ticks[CentralProcessor.TickType.IDLE.index] - prevTicks[CentralProcessor.TickType.IDLE.index]
    val totalCpu = user + nice + cSys + idle + ioWait + irq + soft + steal
    return 1.0 - (idle * 1.0 / totalCpu)
}

fun getMemoryUsage(si: SystemInfo): Double {
    val memory = si.hardware.memory
    return (memory.total - memory.available) * 1.0 / memory.total
}

fun getMemoryUsageString(si: SystemInfo): String {
    val memory = si.hardware.memory
    return "${formatByte(memory.total - memory.available)}/${formatByte(memory.total)}"
}

fun getFileSystemUsageString(si: SystemInfo): String {
    val fileSystem = si.operatingSystem.fileSystem.fileStores
    var usage = ""
    for (i in fileSystem.indices) {
        usage += "${fileSystem[i].mount} ${formatByte(fileSystem[i].totalSpace - fileSystem[i].freeSpace)}/${
            formatByte(
                fileSystem[i].totalSpace
            )
        }\n"
    }
    return usage.dropLast(1)
}