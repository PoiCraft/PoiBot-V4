package com.poicraft.bot.v4.plugin.autoimport

import io.github.classgraph.ClassGraph
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.kotlinFunction

fun getAllAnnotatedWith(annotation: KClass<out Annotation>): List<KFunction<*>> {
    val `package` = annotation.java.`package`.name
    val annotationName = annotation.java.canonicalName

    return ClassGraph()
        .enableAllInfo()
        .acceptPackages(`package`)
        .scan().use { scanResult ->
            scanResult
                .getClassesWithMethodAnnotation(annotationName)
                .flatMap { routeClassInfo ->
                    routeClassInfo.methodInfo.filter { function ->
                        function.hasAnnotation(annotation.java)
                    }.mapNotNull { method ->
                        method.loadClassAndGetMethod().kotlinFunction
                    }
                }
        }
}