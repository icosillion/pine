package com.icosillion.pine.test.harness

import com.icosillion.pine.Pine

class TestablePine : Pine() {

    fun testingPreflight() {
        this.preflight()
    }
}