package com.poicraft.bot.v4.plugin.utils

import net.mamoe.mirai.utils.MiraiLogger
import org.ktorm.logging.LogLevel
import org.ktorm.logging.Logger

class KtormLoggerAdapter(val threshold: LogLevel, val logger: MiraiLogger) : Logger {

    override fun isTraceEnabled(): Boolean {
        return LogLevel.TRACE >= threshold
    }

    override fun trace(msg: String, e: Throwable?) {
        logger.error(msg, e)
    }

    override fun isDebugEnabled(): Boolean {
        return LogLevel.DEBUG >= threshold
    }

    override fun debug(msg: String, e: Throwable?) {
        logger.debug(msg, e)
    }

    override fun isInfoEnabled(): Boolean {
        return LogLevel.INFO >= threshold
    }

    override fun info(msg: String, e: Throwable?) {
        logger.info(msg, e)
    }

    override fun isWarnEnabled(): Boolean {
        return LogLevel.WARN >= threshold
    }

    override fun warn(msg: String, e: Throwable?) {
        logger.warning(msg, e)
    }

    override fun isErrorEnabled(): Boolean {
        return LogLevel.ERROR >= threshold
    }

    override fun error(msg: String, e: Throwable?) {
        logger.error(msg, e)
    }
}