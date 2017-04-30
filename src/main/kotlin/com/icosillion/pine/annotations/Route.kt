package com.icosillion.pine.annotations

import com.icosillion.pine.http.Method

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Route(val path:String, val methods:Array<Method> = arrayOf(Method.GET),
                       val accepts:Array<String> = arrayOf("application/json"))