package com.pswidersk.gradle.yamlsecrets

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junitpioneer.jupiter.SetEnvironmentVariable
import java.io.File

internal class YamlSecretsResolverTest {

    // given
    private val yamlSecretsResolver = initSecretsResolver("secrets")

    @Test
    fun `test if secrets were added to resolver`() {
        // then
        assertThat(yamlSecretsResolver.getValue("testSecrets.testProp1"))
            .isEqualTo("test")
    }

    @Test
    fun `test if nested string props are resolved properly 1`() {
        // then
        assertThat(yamlSecretsResolver.getValue("testSecrets.testProp3.nested1").toString())
            .isEqualTo("2")
        assertThat(yamlSecretsResolver.getValue("testSecrets.testProp3.nested2"))
            .isEqualTo("test")
    }

    @Test
    fun `test if nested string props are resolved properly 2`() {
        // then
        assertThat(yamlSecretsResolver.get<Any>("testSecrets.testProp3.nested1"))
            .isEqualTo(2)
        assertThat(yamlSecretsResolver.get<String>("testSecrets.testProp3.nested2"))
            .isEqualTo("test")
    }

    @Test
    fun `test if nested string props are resolved properly 3`() {
        // then
        assertThat(yamlSecretsResolver.get<Any>("testSecrets", "testProp3.nested1"))
            .isEqualTo(2)
        assertThat(yamlSecretsResolver.get<String>("testSecrets", "testProp3.nested2"))
            .isEqualTo("test")
    }

    @Test
    fun `test if empty prop is resolved properly`() {
        // then
        assertThat(yamlSecretsResolver.getValue("testSecrets.testEmptyProp"))
            .isEqualTo("")
    }

    @Test
    fun `test if nested string prop inside list is resolved properly`() {
        // then
        assertThat(yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[2]"))
            .isEqualTo("testPropInList")
        assertThat(yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[0].key"))
            .isEqualTo("testKey")
        assertThat(yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[0].alsoNestedList.[1]"))
            .isEqualTo("testValue2InNestedList")
    }

    @Test
    fun `test if exception is thrown for illegalIndex 1`() {
        // then
        assertThatThrownBy { yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[1].key") }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Key: key does not exists in secrets: testSecrets.")

        assertThatThrownBy { yamlSecretsResolver.getValue("testSecrets.testProp3.nestedList.[4]") }
            .isInstanceOf(IndexOutOfBoundsException::class.java)
    }

    @Test
    fun `test if exception is thrown for illegalIndex 2`() {
        // then
        assertThatThrownBy { yamlSecretsResolver.getValue("testSecrets", "testProp3.nestedList.[1].key") }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Key: key does not exists in secrets: testSecrets.")

        assertThatThrownBy { yamlSecretsResolver.getValue("testSecrets", "testProp3.nestedList.[4]") }
            .isInstanceOf(IndexOutOfBoundsException::class.java)
    }

    @Test
    fun `test if exception is thrown for non-existing secret file`() {
        // then
        assertThatThrownBy { yamlSecretsResolver.getValue("testNonExistingSecrets.nonExisting") }
            .isInstanceOf(NoSuchElementException::class.java)
            .hasMessage("Key testNonExistingSecrets is missing in the map.")
    }

    @Test
    fun `test if exception is thrown for non-existing key`() {
        // then
        assertThatThrownBy { yamlSecretsResolver.getValue("testSecrets.nonExisting") }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Key: nonExisting does not exists in secrets: testSecrets.")
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
        assertThat(testProp2).isEqualTo(3)
    }

    @Test
    fun `test accessing by delegate 2`() {
        // then
        val testProp2 by yamlSecretsResolver.get<Map<String, Any>>("testSecrets")
        assertThat(testProp2).isEqualTo(3)
    }

    @Test
    fun `test accessing by delegate 3`() {
        // then
        val testProp2 by yamlSecretsResolver.getSecretsData("testSecrets").properties
        assertThat(testProp2).isEqualTo(3)
    }

    @Test
    fun `test getting secrets names`() {
        // given
        val expectedList = setOf("testSecrets", "testSecrets2")
        // then
        assertThat(yamlSecretsResolver.getNames()).isEqualTo(expectedList)
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
        assertThat(yamlSecretsResolver.getSecretsData("testSecrets2"))
            .isEqualTo(expectedYamlSecretsData)
    }

    @Test
    fun `test getting missing secrets data`() {
        // then
        assertThatThrownBy { yamlSecretsResolver.getSecretsData("nonExistingSecrets") }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Secrets with name: \"nonExistingSecrets\" could not be found.")
    }

    @Test
    fun `test resolving empty secret file`() {
        // then
        assertThatThrownBy { initSecretsResolver("emptySecrets") }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("Exception occurred during parsing YAML file (file can not be empty)")
    }

    @Test
    @SetEnvironmentVariable(key = "TESTSECRETS_ENV_TESTPROP", value = "test Property Value From Environment")
    fun `test getting secret by env if not specified in file`() {
        // then
        assertThat(yamlSecretsResolver.getValue("testSecrets.env.testProp"))
            .isEqualTo("test Property Value From Environment")
    }

    @Test
    @SetEnvironmentVariable(key = "CUSTOM_ENV_VAR_NAME", value = "test Property Value From Environment")
    fun `test getting secret by env with custom env variable name`() {
        // then
        assertThat(yamlSecretsResolver.getValue("testSecrets", "env.testProp", "CUSTOM_ENV_VAR_NAME"))
            .isEqualTo("test Property Value From Environment")
    }

    @Test
    @SetEnvironmentVariable(key = "TESTSECRETS_ENV_TESTPROP", value = "test Property Value From Environment")
    fun `test getting secret by env if not specified in file (reified)`() {
        // then
        assertThat(yamlSecretsResolver.get<String>("testSecrets.env.testProp"))
            .isEqualTo("test Property Value From Environment")
    }

    @Test
    @SetEnvironmentVariable(key = "CUSTOM_ENV_VAR_NAME", value = "test Property Value From Environment")
    fun `test getting secret by env with custom env variable name  (reified)`() {
        // then
        assertThat(yamlSecretsResolver.get<String>("testSecrets", "env.testProp", "CUSTOM_ENV_VAR_NAME"))
            .isEqualTo("test Property Value From Environment")
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