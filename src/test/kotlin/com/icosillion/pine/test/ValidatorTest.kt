package com.icosillion.pine.test

import com.github.salomonbrys.kotson.jsonArray
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.toJson
import com.icosillion.pine.validator.Rule
import com.icosillion.pine.validator.arraySchema
import com.icosillion.pine.validator.objectSchema
import org.junit.Test
import org.junit.Assert.*

class ValidatorTest {

    @Test
    fun stringTest() {
        val schema = objectSchema(
                "string" to Rule().string()
        )

        assertTrue(schema.validate(jsonObject(
                "string" to "test"
        )))

        assertFalse(schema.validate(jsonObject(
                "string" to 42
        )))

        assertFalse(schema.validate(jsonObject(
                "string" to jsonObject()
        )))
    }

    @Test
    fun arrayTest() {
        val elementSchema = objectSchema(
                "name" to Rule().string(),
                "age" to Rule().integer()
        )

        val schema = arraySchema(elementSchema)

        assertTrue(schema.validate(jsonArray(
                jsonObject(
                        "name" to "John Smith",
                        "age" to 56
                ),
                jsonObject(
                        "name" to "Mary Denzel",
                        "age" to 24
                )
        )))

        assertFalse(schema.validate(jsonArray(
                jsonObject(
                        "name" to "John Smith",
                        "age" to 56
                ),
                jsonObject(
                        "name" to "Mary Denzel",
                        "age" to 24
                ),
                jsonObject(
                        "naam" to "Mr Invalid",
                        "age" to 12
                )
        )))
    }

    @Test
    fun maxTest() {
        //Test Strings
        val stringRule = Rule().string().max(5)

        assertTrue(stringRule.validate("Test".toJson()))
        assertTrue(stringRule.validate("Test1".toJson()))

        assertFalse(stringRule.validate("Test22".toJson()))

        //Test Integers
        val integerRule = Rule().integer().max(5)
        
        assertTrue(integerRule.validate(4.toJson()))
        assertTrue(integerRule.validate(5.toJson()))

        assertFalse(integerRule.validate(6.toJson()))
    }

    @Test
    fun integerTest() {
        val rule = Rule().integer()

        assertTrue(rule.validate(5.toJson()))
        assertTrue(rule.validate((-1).toJson()))
        assertTrue(rule.validate(12.toJson()))

        assertFalse(rule.validate((12.5).toJson()))
        assertFalse(rule.validate("Test".toJson()))
    }
}