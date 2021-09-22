package com.pswidersk.gradle.yamlsecrets

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
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
        assertEquals(
            "testValue2InNestedList",
            yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[0].alsoNestedList.[1]")
        )
    }

    @Test
    fun `test if exception is thrown for illegalIndex 1`() {
        // then
        assertThat { yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[1].key") }
            .isFailure()
            .all {
                isInstanceOf(IllegalStateException::class)
                hasMessage("Key: key does not exists in secrets: testSecrets.")
            }
        assertThat { yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[4]") }
            .isFailure()
            .all {
                isInstanceOf(IndexOutOfBoundsException::class)
                hasMessage("Index: 4, Size: 3")
            }
    }

    @Test
    fun `test if exception is thrown for illegalIndex 2`() {
        // then
        assertThat { yamlSecretsResolver.getValue("testSecrets", "testProp3.nestedList.[1].key") }
            .isFailure()
            .all {
                isInstanceOf(IllegalStateException::class)
                hasMessage("Key: key does not exists in secrets: testSecrets.")
            }
        assertThat { yamlSecretsResolver.getValue("testSecrets", "testProp3.nestedList.[4]") }
            .isFailure()
            .all {
                isInstanceOf(IndexOutOfBoundsException::class)
                hasMessage("Index: 4, Size: 3")
            }
    }

    @Test
    fun `test if exception is thrown for non-existing secret file`() {
        // then
        assertThat { yamlSecretsResolver.getValue("testNonExistingSecrets.nonExisting") }
            .isFailure()
            .all {
                isInstanceOf(NoSuchElementException::class)
                hasMessage("Key testNonExistingSecrets is missing in the map.")
            }
    }

    @Test
    fun `test if exception is thrown for non-existing key`() {
        // then
        assertThat { yamlSecretsResolver.getValue("testSecrets.nonExisting") }
            .isFailure()
            .all {
                isInstanceOf(IllegalStateException::class)
                hasMessage("Key: nonExisting does not exists in secrets: testSecrets.")
            }
    }

    @Test
    fun `test if expected map is returned`() {
        // then
        assertThat(yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[0]"))
            .isEqualTo(
                mapOf(
                    "key" to "testKey",
                    "value" to "testValue",
                    "alsoNestedList" to listOf("testValueInNestedList", "testValue2InNestedList")
                )
            )
    }

    @Test
    fun `test if expected list is returned`() {
        // then
        assertThat(yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[0].alsoNestedList"))
            .isEqualTo(listOf("testValueInNestedList", "testValue2InNestedList"))
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
        val testProp2 by yamlSecretsResolver.getSecretsData("testSecrets").properties
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
    fun `test getting secrets data`() {
        // given
        val secretName = "testSecrets2"
        val expectedYamlSecretsData = YamlSecretsData(
            secretName,
            getFileByResource("secrets").resolve("$secretName.sec.yml"),
            getFileByResource("secrets").resolve(".$secretName.sec.yml"),
            mapOf("testProp1" to "test2")
        )
        // then
        assertEquals(expectedYamlSecretsData, yamlSecretsResolver.getSecretsData("testSecrets2"))
    }

    @Test
    fun `test getting missing secrets data`() {
        // then
        assertThat { yamlSecretsResolver.getSecretsData("nonExistingSecrets") }
            .isFailure()
            .all {
                isInstanceOf(IllegalStateException::class)
                hasMessage("Secrets with name: \"nonExistingSecrets\" could not be found.")
            }
    }

    @Test
    fun `test resolving empty secret file`() {
        // then
        assertThat { initSecretsResolver("emptySecrets") }
            .isFailure()
            .all {
                isInstanceOf(IllegalStateException::class)
                messageContains("Exception occurred during parsing YAML file (file can not be empty)")
            }
    }

    private fun initSecretsResolver(resourceDirectoryName: String): YamlSecretsResolver {
        val yamlSecretsResolver = YamlSecretsResolver()
        val secretsDir = getFileByResource(resourceDirectoryName)
        loadSecretsByDirs(yamlSecretsResolver, sequenceOf(secretsDir))
        return yamlSecretsResolver
    }

    private fun getFileByResource(resourceName: String): File =
        File(javaClass.classLoader.getResource(resourceName)!!.path)

}