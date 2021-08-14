package com.poicraft.bot.v4.plugin.constants

/**
 * 权限常量
 * @author gggxbbb
 */
enum class Permission {
    /**
     * 任意群员
     */
    PERMISSION_LEVEL_EVERYONE,

    /**
     * 管理员及群主
     */
    PERMISSION_LEVEL_ADMIN,

    /**
     * 群主
     */
    PERMISSION_LEVEL_OWNER,

    /**
     * 服务器管理群内成员
     */
    PERMISSION_LEVEL_ADMIN_GROUP
}