package com.poicraft.bot.v4.plugin.constants

/**
 * 玩家白名单状态
 */
enum class WhitelistStatus {
    /**
     * 已在白名单中
     */
    PLAYER_ALREADY_IN_WHITELIST,

    /**
     * 不在白名单中
     */
    PLAYER_NOT_IN_WHITELIST,

    /**
     * 已添加至白名单 (瞬时)
     */
    PLAY_ADDED,

    /**
     * 已从白名单移除 (瞬时)
     */
    PLAY_REMOVED
}