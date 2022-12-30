package com.poicraft.bot.v4.plugin.utils

import com.poicraft.bot.v4.plugin.provider.command.CommandBox

/**
 * 获得近似命令
 */
fun getSimilarCommands(ipt: String): List<String> {
    val opt = mutableListOf<String>()
    for (o in CommandBox.keys) {
        if (levenshtein(ipt, o) > 0.5) {
            opt.add(o)
        }
    }
    return opt
}

/**
 * 获得近似命令名
 */
fun getSimilarCommandNames(ipt: String): List<String> {
    val opt = mutableListOf<String>()
    for (o in CommandBox.values) {
        if ((levenshtein(ipt, o.name) > 0.5) and !opt.contains(o.name)) {
            opt.add(o.name)
        }
    }
    return opt
}

fun levenshtein(a: String, b: String): Float {
    val editDistance = editDis(a, b)
    return 1 - editDistance.toFloat() / a.length.coerceAtLeast(b.length)
}

private fun editDis(a: String, b: String): Int {
    val aLen = a.length
    val bLen = b.length
    val v = Array(aLen + 1) { IntArray(bLen + 1) }
    for (i in 0..aLen) {
        for (j in 0..bLen) {
            if (i == 0) {
                v[i][j] = j
            } else if (j == 0) {
                v[i][j] = i
            } else if (a[i - 1] == b[j - 1]) {
                v[i][j] = v[i - 1][j - 1]
            } else {
                v[i][j] = 1 + v[i - 1][j - 1].coerceAtMost(v[i][j - 1].coerceAtMost(v[i - 1][j]))
            }
        }
    }
    return v[aLen][bLen]
}