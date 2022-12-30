package com.poicraft.bot.v4.plugin.utils.oshi

import com.poicraft.bot.v4.plugin.utils.formatByte
import oshi.SystemInfo
import oshi.hardware.CentralProcessor
import oshi.software.os.OperatingSystem
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

@Suppress("DuplicatedCode")
fun getFullCPUUsage(si: SystemInfo): String {
    val processors = si.hardware.processor
    var result = ""
    val prevTicks: LongArray = processors.systemCpuLoadTicks
    TimeUnit.SECONDS.sleep(1)
    val ticks: LongArray = processors.systemCpuLoadTicks
    val user = ticks[CentralProcessor.TickType.USER.index] - prevTicks[CentralProcessor.TickType.USER.index]
    val nice = ticks[CentralProcessor.TickType.NICE.index] - prevTicks[CentralProcessor.TickType.NICE.index]
    val sys = ticks[CentralProcessor.TickType.SYSTEM.index] - prevTicks[CentralProcessor.TickType.SYSTEM.index]
    val idle = ticks[CentralProcessor.TickType.IDLE.index] - prevTicks[CentralProcessor.TickType.IDLE.index]
    val iowait = ticks[CentralProcessor.TickType.IOWAIT.index] - prevTicks[CentralProcessor.TickType.IOWAIT.index]
    val irq = ticks[CentralProcessor.TickType.IRQ.index] - prevTicks[CentralProcessor.TickType.IRQ.index]
    val softirq = ticks[CentralProcessor.TickType.SOFTIRQ.index] - prevTicks[CentralProcessor.TickType.SOFTIRQ.index]
    val steal = ticks[CentralProcessor.TickType.STEAL.index] - prevTicks[CentralProcessor.TickType.STEAL.index]
    val totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal

    result += "User: %.1f%% Nice: %.1f%% System: %.1f%% Idle: %.1f%% IOwait: %.1f%% IRQ: %.1f%% SoftIRQ: %.1f%% Steal: %.1f%%%n\n".format(
        null,
        100.0 * (user / totalCpu),
        100.0 * (nice / totalCpu),
        100.0 * (sys / totalCpu),
        100.0 * (idle / totalCpu),
        100.0 * (iowait / totalCpu),
        100.0 * (irq / totalCpu),
        100.0 * (softirq / totalCpu),
        100.0 * (steal / totalCpu)
    )

    result += "CPU Load: %.1f%%\n".format(null, 100.0 * (user + nice + sys) / totalCpu)

    result += "CPU Load per core:\n"
    val load = processors.getProcessorCpuLoad(2)
    for (i in load.indices) {
        result += "Core $i: %.1f%%\n".format(null, 100.0 * load[i])
    }

    return result.dropLast(1)
}

fun getProcessesInfoString(si: SystemInfo): String {
    val memory = si.hardware.memory
    val processList = si.operatingSystem.getProcesses(null, OperatingSystem.ProcessSorting.CPU_DESC, 5)
    var result = "Top 5 CPU Processes:\n"
    result += "%CPU %MEM       VSZ       RSS Name\n"
    for (process in processList) {
        result += "%5.1f %4.1f %9s %9s %s%n\n".format(
            null,
            100.0 * (process.kernelTime + process.userTime) / process.upTime,
            100.0 * process.residentSetSize / memory.total, formatByte(process.virtualSize),
            formatByte(process.residentSetSize), process.name
        )
    }
    return result.dropLast(1)
}

fun getSwapUsage(si: SystemInfo): Double {
    val memory = si.hardware.memory
    return (memory.virtualMemory.swapUsed / memory.virtualMemory.swapTotal).toDouble()
}

fun getSwapUsageString(si: SystemInfo): String {
    val memory = si.hardware.memory
    return "${formatByte(memory.virtualMemory.swapUsed)}/${formatByte(memory.virtualMemory.swapTotal)}"
}

fun getFileSystemUsageString(si: SystemInfo): String {
    val fileSystem = si.operatingSystem.fileSystem.fileStores
    var usage = ""
    for (fs in fileSystem) {
        usage += "${fs.mount} ${formatByte(fs.totalSpace - fs.usableSpace)}/${formatByte(fs.totalSpace)} ${
            DecimalFormat(
                "#.##%"
            ).format((fs.totalSpace - fs.usableSpace) / fs.totalSpace)
        }\n"
    }
    return usage.dropLast(1)
}