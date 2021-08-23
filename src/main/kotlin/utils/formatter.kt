package com.poicraft.bot.v4.plugin.utils

import java.text.DecimalFormat

/**
 * 格式化单位
 */
fun formatByte(byteNumber: Long): String? {
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