package com.icosillion.pine.annotations

import com.icosillion.pine.middleware.Middleware
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Use(val middleware:KClass<out Middleware>)
