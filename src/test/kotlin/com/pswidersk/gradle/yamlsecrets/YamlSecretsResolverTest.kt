package com.pswidersk.gradle.yamlsecrets

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

internal class YamlSecretsResolverTest {

    //given
    private val yamlSecretsResolver = initSecretsResolver("secrets")

    @Test
    fun `test if secrets were added to resolver`() {
        // then
        assertEquals("test", yamlSecretsResolver.getValue("testSecrets.testProp1"))
    }

    @Test
    fun `test if nested string props are resolved properly 1`() {
        // then
        assertEquals("2", yamlSecretsResolver.getValue("testSecrets.testProp3.nested1").toString())
        assertEquals("test", yamlSecretsResolver.getValue("testSecrets.testProp3.nested2"))
    }

    @Test
    fun `test if nested string props are resolved properly 2`() {
        // then
        assertEquals(2, yamlSecretsResolver.get("testSecrets.testProp3.nested1"))
        assertEquals("test", yamlSecretsResolver.get<String>("testSecrets.testProp3.nested2"))
    }

    @Test
    fun `test if nested string props are resolved properly 3`() {
        // then
        assertEquals(2, yamlSecretsResolver.get("testSecrets", "testProp3.nested1"))
        assertEquals("test", yamlSecretsResolver.get<String>("testSecrets", "testProp3.nested2"))
    }

    @Test
    fun `test if empty prop is resolved properly `() {
        // then
        assertEquals("", yamlSecretsResolver.getValue("testSecrets.testEmptyProp"))
    }

    @Test
    fun `test if nested string prop inside list is resolved properly`() {
        // then
        assertEquals("testPropInList", yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[2]"))
        assertEquals("testKey", yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[0].key"))
        assertEquals("testValue2InNestedList", yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[0].alsoNestedList.[1]"))
    }

    @Test
    fun `test if exception is thrown for illegalIndex 1`() {
        // then
        assertThrows<IllegalStateException> { yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[1].key") }
        assertThrows<IndexOutOfBoundsException> { yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[4]") }
    }

    @Test
    fun `test if exception is thrown for illegalIndex 2`() {
        // then
        assertThrows<IllegalStateException> { yamlSecretsResolver.getValue("testSecrets", "testProp3.nestedList.[1].key") }
        assertThrows<IndexOutOfBoundsException> { yamlSecretsResolver.getValue("testSecrets", "testProp3.nestedList.[4]") }
    }

    @Test
    fun `test if exception is thrown for non-existing secret file`() {
        // then
        assertThrows<NoSuchElementException> { yamlSecretsResolver.getValue("testNonExistingSecrets.nonExisting") }
    }

    @Test
    fun `test if exception is thrown for non-existing key`() {
        // then
        val exception = assertThrows<IllegalStateException> { yamlSecretsResolver.getValue("testSecrets.nonExisting") }
        assertEquals("Key: nonExisting does not exists in secrets: testSecrets.", exception.message)
    }

    @Test
    fun `test if expected map is returned`() {
        // given
        val expectedMap = mapOf("key" to "testKey",
                "value" to "testValue",
                "alsoNestedList" to listOf("testValueInNestedList", "testValue2InNestedList"))
        // then
        assertEquals(expectedMap, yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[0]"))
    }

    @Test
    fun `test if expected list is returned`() {
        // given
        val expectedList = listOf("testValueInNestedList", "testValue2InNestedList")
        // then
        assertEquals(expectedList, yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[0].alsoNestedList"))
    }

    @Test
    fun `test accessing by delegate 1`() {
        // then
        val testProp2 by yamlSecretsResolver.get<Map<String, Any>>("testSecrets.")
        assertEquals(3, testProp2)
    }

    @Test
    fun `test accessing by delegate 2`() {
        // then
        val testProp2 by yamlSecretsResolver.get<Map<String, Any>>("testSecrets")
        assertEquals(3, testProp2)
    }

    @Test
    fun `test accessing by delegate 3`() {
        // then
        val testProp2 by yamlSecretsResolver.getSecrets("testSecrets")
        assertEquals(3, testProp2)
    }

    @Test
    fun `test getting secrets names`() {
        // given
        val expectedList = setOf("testSecrets", "testSecrets2")
        // then
        assertEquals(expectedList, yamlSecretsResolver.getNames())
    }

    @Test
    fun `test resolving empty secret file`() {
        // then
        assertThrows<IllegalStateException> { initSecretsResolver("emptySecrets") }
    }

    private fun initSecretsResolver(resourceName: String): YamlSecretsResolver {
        val yamlSecretsResolver = YamlSecretsResolver()
        getSecretTemplatesInDir(File(javaClass.classLoader.getResource(resourceName)!!.path)).forEach {
            loadProperties(yamlSecretsResolver, it)
        }
        return yamlSecretsResolver
    }

}