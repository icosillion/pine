package com.icosillion.pine.resources

import com.icosillion.pine.ClosureCallableResource
import com.icosillion.pine.Pine
import com.icosillion.pine.http.Method
import com.icosillion.pine.http.Request
import com.icosillion.pine.http.Response

/**
 * Provides a wrapper around the handler functions so that they can be easily registered in the router
 */
class FunctionResource(private val method: Method, private val path: String, private val function: (Request, Response) -> Response) : DynamicResource {

    override fun registerRoutes(pine: Pine) {
        pine.route(ClosureCallableResource(function), path, arrayListOf(method))
    }
}