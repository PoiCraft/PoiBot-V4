package com.poicraft.bot.v4.plugin.provider.service


/**
 * 如何编写一个服务
 *
 * 首先，你需要在 plugins.services 包下新建一个 Kotlin 文件，其名称应根据你需要实现的一个或一组服务的功能来定。为了阐述方便，此处采用 `abc` 这个名称。
 *
 * 之后，在 abc.kt 中定义一个函数, 并且这个函数应该被 `@Service` 注解, 同时必须接收参数 plugin: PluginMain.
 *
 * ```
 * @Service
 * fun abc(plugin: PluginMain) {
 *    //xxx
 *    }
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Service
