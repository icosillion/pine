package com.icosillion.pine.resources

import com.icosillion.pine.Pine

/**
 * Allows resources to register routes dynamically
 */
interface DynamicResource {

    fun registerRoutes(pine: Pine)

}