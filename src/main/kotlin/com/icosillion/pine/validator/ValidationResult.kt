package com.icosillion.pine.validator

import java.util.*

data class ValidationError(val field: String?,
                      val reason: String) {}

data class ValidationResult(var isValid: Boolean,
                       val errors: ArrayList<ValidationError> = arrayListOf()) {
}