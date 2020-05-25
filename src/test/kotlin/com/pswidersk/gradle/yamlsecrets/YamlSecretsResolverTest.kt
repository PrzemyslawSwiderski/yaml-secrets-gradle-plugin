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
        assertEquals("test", yamlSecretsResolver.getStringValue("testSecrets.testProp1"))
    }

    @Test
    fun `test if nested string props are resolved properly`() {
        // given
        val yamlSecretsResolver = initSecretsResolver()

        // then
        assertEquals("2", yamlSecretsResolver.getStringValue("testSecrets.testProp3.nested1"))
        assertEquals("test", yamlSecretsResolver.getStringValue("testSecrets.testProp3.nested2"))
    }

    @Test
    fun `test if nested string prop inside list is resolved properly`() {
        // given
        val yamlSecretsResolver = initSecretsResolver()

        // then
        assertEquals("testPropInList", yamlSecretsResolver.getStringValue("testSecrets.testProp3.nestedList.[2]"))
        assertEquals("testKey", yamlSecretsResolver.getStringValue("testSecrets.testProp3.nestedList.[0].key"))
        assertEquals("testValue2InNestedList", yamlSecretsResolver.getStringValue("testSecrets.testProp3.nestedList.[0].alsoNestedList.[1]"))
    }

    @Test
    fun `test if exception is thrown for illegalIndex`() {
        // given
        val yamlSecretsResolver = initSecretsResolver()

        // then
        assertThrows<IllegalStateException> { yamlSecretsResolver.getStringValue("testSecrets.testProp3.nestedList.[1].key") }
        assertThrows<IndexOutOfBoundsException> { yamlSecretsResolver.getStringValue("testSecrets.testProp3.nestedList.[4]") }
    }

    @Test
    fun `test if exception is thrown for non-existing secret file`() {
        // given
        val yamlSecretsResolver = initSecretsResolver()

        // then
        assertThrows<NoSuchElementException> { yamlSecretsResolver.getStringValue("testNonExistingSecrets.nonExisting") }
    }

    @Test
    fun `test if exception is thrown for non-existing key`() {
        // given
        val yamlSecretsResolver = initSecretsResolver()

        // then
        val exception = assertThrows<IllegalStateException> { yamlSecretsResolver.getStringValue("testSecrets.nonExisting") }
        assertEquals("Key: nonExisting does not exists in secrets: testSecrets.", exception.message)
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