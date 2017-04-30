package com.icosillion.pine

import com.icosillion.pine.http.Request
import com.icosillion.pine.http.Response
import kotlin.reflect.KCallable

interface CallableResource {
    fun call(request: Request, response: Response)
}

class ReflectedCallableResource(val obj: Any, val function: KCallable<*>) : CallableResource {

    override fun call(request: Request, response: Response) {
        function.call(obj, request, response)
    }
}

class ClosureCallableResource(val function: (Request, Response) -> Response) : CallableResource {

    override fun call(request: Request, response: Response) {
        function.invoke(request, response)
    }
}