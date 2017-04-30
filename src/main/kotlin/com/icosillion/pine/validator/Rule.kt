package com.icosillion.pine.validator

import com.google.gson.JsonElement

class Rule {

    protected var optional = false
    protected var nullable = false
    private val rules = hashMapOf<String, (Any) -> Boolean>()

    //Rules
    fun string(): Rule {
        rules.put("string") { value ->
            if(value is JsonElement) {
                if(value.isJsonPrimitive) {
                    return@put value.asJsonPrimitive.isString
                }
            }

            return@put value is String
        }

        return this
    }

    fun integer(): Rule {
        rules.put("integer") { value ->
            if(value is JsonElement) {
                if(value.isJsonPrimitive) {
                    if(value.asJsonPrimitive.isNumber) {
                        return@put value.asJsonPrimitive.asNumber.toDouble().mod(1) == 0.0
                    }
                }
            }

            return@put value is Int
        }

        return this
    }

    fun optional(): Rule {
        optional = true

        return this
    }

    fun nullable(): Rule {
        nullable = true

        return this
    }

    fun min(min: Number): Rule {
        rules.put("min") { value ->

            val numberValue = valueAsNumber(value)
            val stringValue = valueAsString(value)

            if(numberValue != null) {
                if(numberValue.toDouble() < min.toDouble()) {
                    return@put false
                }

                return@put true
            }

            if(stringValue != null) {
                if(stringValue.length < min.toInt()) {
                    return@put false
                }

                return@put true
            }

            return@put false
        }

        return this
    }

    fun max(max: Number): Rule {
        rules.put("max") { value ->

            val numberValue = valueAsNumber(value)
            val stringValue = valueAsString(value)

            if(numberValue != null) {
                if(numberValue.toDouble() > max.toDouble()) {
                    return@put false
                }

                return@put true
            }

            if(stringValue != null) {
                if(stringValue.length > max.toInt()) {
                    return@put false
                }

                return@put true
            }

            return@put false
        }

        return this
    }

    fun boolean(): Rule {
        rules.put("boolean") { value ->

            val boolValue = valueAsBoolean(value)
            return@put boolValue != null
        }

        return this
    }

    fun number(): Rule {
        rules.put("number") { value ->

            val numberValue = valueAsNumber(value)
            return@put numberValue != null
        }

        return this
    }

    fun length(length: Int): Rule {
        rules.put("length") { value ->
            val stringValue = valueAsString(value)
            if(stringValue == null) {
                return@put false
            }

            return@put stringValue.length == length
        }

        return this
    }

    fun anyOf(items: Array<String>, ignoreCase: Boolean = true): Rule {
        rules.put("anyOf") { value ->
            val stringValue = valueAsString(value)
            if(stringValue == null) {
                return@put false
            }

            items.forEach { item ->
                if(stringValue.equals(stringValue, ignoreCase)) {
                    return@put true
                }
            }

            return@put false
        }

        return this
    }

    fun validateWithReporting(obj: JsonElement?): ValidationResult {
        if(obj == null) {
            if(optional) {
                return ValidationResult(true)
            } else {
                return ValidationResult(false, arrayListOf(ValidationError(
                        field = null,
                        reason = "Json Element not passed"
                )))
            }
        }

        if(obj.isJsonNull) {
            if(nullable) {
                return ValidationResult(true)
            } else {
                return ValidationResult(false, arrayListOf(ValidationError(
                        field = null,
                        reason = "Element is not nullable"
                )))
            }
        }

        var isValid = true
        val errors = arrayListOf<ValidationError>()
        rules.forEach { type, action ->
            if(action.invoke(obj).not()) {
                isValid = false
                errors.add(ValidationError(
                        field = null,
                        reason = "Rule $type failed"
                ))
                return@forEach
            }
        }

        return ValidationResult(isValid, errors)
    }

    fun validate(obj: JsonElement?): Boolean {
        return validateWithReporting(obj).isValid
    }

    //Helper Methods
    private fun valueAsInteger(value: Any): Int? {
        if(value is Int) {
            return value
        }

        if(value is JsonElement
                && value.isJsonPrimitive
                && value.asJsonPrimitive.isNumber
                && value.asJsonPrimitive.asNumber is Int
        ) {
            return value.asJsonPrimitive.asInt
        }

        return null
    }

    private fun valueAsNumber(value: Any): Number? {
        if(value is Number) {
            return value
        }

        if(value is JsonElement
                && value.isJsonPrimitive
                && value.asJsonPrimitive.isNumber
        ) {
            return value.asJsonPrimitive.asNumber
        }

        return null
    }

    private fun valueAsString(value: Any): String? {
        if(value is String) {
            return value
        }

        if(value is JsonElement
                && value.isJsonPrimitive
                && value.asJsonPrimitive.isString
        ) {
            return value.asJsonPrimitive.asString
        }

        return null
    }

    private fun valueAsBoolean(value: Any): Boolean? {
        if(value is Boolean) {
            return value
        }

        if(value is JsonElement
                && value.isJsonPrimitive
                && value.asJsonPrimitive.isBoolean
        ) {
            return value.asJsonPrimitive.asBoolean
        }

        return null
    }
}
