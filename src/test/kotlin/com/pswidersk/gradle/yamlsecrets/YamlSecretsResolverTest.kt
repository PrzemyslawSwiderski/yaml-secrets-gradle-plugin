package com.pswidersk.gradle.yamlsecrets

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class YamlSecretsResolverTest {

    @Test
    fun `test if secrets were added to resolver`() {
        // given
        val yamlSecretsResolver = initSecretsResolver()

        // then
        assertEquals("test", yamlSecretsResolver.getValue("testSecrets.testProp1"))
    }

    @Test
    fun `test if nested string props are resolved properly`() {
        // given
        val yamlSecretsResolver = initSecretsResolver()

        // then
        assertEquals("2", yamlSecretsResolver.getValue("testSecrets.testProp3.nested1").toString())
        assertEquals("test", yamlSecretsResolver.getValue("testSecrets.testProp3.nested2"))
    }

    @Test
    fun `test if nested string prop inside list is resolved properly`() {
        // given
        val yamlSecretsResolver = initSecretsResolver()

        // then
        assertEquals("testPropInList", yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[2]"))
        assertEquals("testKey", yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[0].key"))
        assertEquals("testValue2InNestedList", yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[0].alsoNestedList.[1]"))
    }

    @Test
    fun `test if exception is thrown for illegalIndex`() {
        // given
        val yamlSecretsResolver = initSecretsResolver()

        // then
        assertThrows<IllegalStateException> { yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[1].key") }
        assertThrows<IndexOutOfBoundsException> { yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[4]") }
    }

    @Test
    fun `test if exception is thrown for non-existing secret file`() {
        // given
        val yamlSecretsResolver = initSecretsResolver()

        // then
        assertThrows<NoSuchElementException> { yamlSecretsResolver.getValue("testNonExistingSecrets.nonExisting") }
    }

    @Test
    fun `test if exception is thrown for non-existing key`() {
        // given
        val yamlSecretsResolver = initSecretsResolver()

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
        val yamlSecretsResolver = initSecretsResolver()

        // then
        assertEquals(expectedMap, yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[0]"))
    }

    @Test
    fun `test if expected list is returned`() {
        // given
        val expectedList = listOf("testValueInNestedList", "testValue2InNestedList")
        val yamlSecretsResolver = initSecretsResolver()

        // then
        assertEquals(expectedList, yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[0].alsoNestedList"))
    }

    @Test
    fun `test accessing by delegate`() {
        // given
        val yamlSecretsResolver = initSecretsResolver()

        // then
        val testProp2 by (yamlSecretsResolver.getValue("testSecrets.") as Map<String, *>)
        assertEquals(3, testProp2)
    }

    private fun initSecretsResolver(): YamlSecretsResolver {
        val mapper = YAMLMapper()
        val testSecretsFileContent = getResourceContent("testSecrets.sec.yml")
        val secrets = mapper.readValue<Map<String, *>>(testSecretsFileContent)
        val yamlSecretsResolver = YamlSecretsResolver()
        yamlSecretsResolver.addSecrets("testSecrets", secrets)
        return yamlSecretsResolver
    }

    private fun getResourceContent(resourceName: String): String {
        return javaClass.classLoader.getResource(resourceName)!!.readText()
    }
}