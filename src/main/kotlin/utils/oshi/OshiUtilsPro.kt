package com.poicraft.bot.v4.plugin.utils.oshi

import com.poicraft.bot.v4.plugin.utils.formatByte
import oshi.SystemInfo
import oshi.hardware.CentralProcessor
import oshi.software.os.OperatingSystem.ProcessFiltering
import oshi.software.os.OperatingSystem.ProcessSorting
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

@Suppress("DuplicatedCode")
fun getFullCPUUsage(si: SystemInfo): String {
    val processors = si.hardware.processor
    var result = ""
    val prevTicks: LongArray = processors.systemCpuLoadTicks
    val prevProcTicks = processors.processorCpuLoadTicks
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
        100.0 * user / totalCpu,
        100.0 * nice / totalCpu,
        100.0 * sys / totalCpu,
        100.0 * idle / totalCpu,
        100.0 * iowait / totalCpu,
        100.0 * irq / totalCpu,
        100.0 * softirq / totalCpu,
        100.0 * steal / totalCpu
    )

    result += "CPU Load: ${DecimalFormat("#.##%").format(1.0 - (idle * 1.0 / totalCpu))}\n"

    result += "CPU Load per core:\n"
    val load = processors.getProcessorCpuLoadBetweenTicks(prevProcTicks)
    for (i in load.indices) {
        result += "Core $i: ${DecimalFormat("#.##%").format(load[i])}\n"
    }

    return result.dropLast(1)
}

fun getProcessesInfoString(si: SystemInfo): String {
    val memory = si.hardware.memory
    val processList = si.operatingSystem.getProcesses(ProcessFiltering.ALL_PROCESSES, ProcessSorting.CPU_DESC, 5)
    var result = "Top 5 CPU Processes:\n"
    result += "%CPU %MEM       VSZ       RSS Name\n"
    for (process in processList) {
        result += "%5.1f %4.1f %9s %9s %s%n\n".format(
            null,
            100.0 * (process.kernelTime + process.userTime) / process.upTime,
            100.0 * process.residentSetSize / memory.total, formatByte(process.virtualSize),
            formatByte(process.residentSetSize), process.name
        ).replace("\n\n", "\n")
    }
    return result.dropLast(1)
}

fun getSwapUsage(si: SystemInfo): Double {
    val memory = si.hardware.memory
    return memory.virtualMemory.swapUsed * 1.0 / memory.virtualMemory.swapTotal
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
            ).format((fs.totalSpace - fs.usableSpace) * 1.0 / fs.totalSpace)
        }\n"
    }
    return usage.dropLast(1)
}

fun getUsageByPID(si: SystemInfo, pid: Int): String {
    val os = si.operatingSystem
    val process = os.getProcess(pid)

    val startAt = Date(process.startTime)
    val timeFormatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS")

    val uptime = process.upTime

    return """
        PID: ${process.processID}
        Name: ${process.name}
        CPU: ${DecimalFormat("#.##%").format((process.kernelTime + process.userTime) * 1.0 / process.upTime)}
        Memory: ${formatByte(process.residentSetSize)} / ${formatByte(process.virtualSize)}(VSZ) / ${formatByte(process.residentSetSize)}(RSS)
        Start: ${timeFormatter.format(startAt)}
        Uptime: ${TimeUnit.MILLISECONDS.toDays(uptime)}d ${TimeUnit.MILLISECONDS.toHours(uptime) % 24}h ${
        TimeUnit.MILLISECONDS.toMinutes(
            uptime
        ) % 60
    }m ${TimeUnit.MILLISECONDS.toSeconds(uptime) % 60}s ${uptime % 1000}ms
    """.trimIndent()
}

fun getSelfUsage(si: SystemInfo): String {
    val os = si.operatingSystem
    return getUsageByPID(si, os.processId)
}

fun getBedrockUsage(si: SystemInfo): String {
    val os = si.operatingSystem
    os.getProcesses(ProcessFiltering.ALL_PROCESSES, ProcessSorting.CPU_DESC, 10).forEach {
        if (it.name == "bedrock_server" || it.name == "bedrock_server_mod") {
            return getUsageByPID(si, it.processID)
        }
    }
    return "No bedrock server found in top 10 processes"
}