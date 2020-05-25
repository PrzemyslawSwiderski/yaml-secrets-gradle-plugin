package com.pswidersk.gradle.yamlsecrets

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

internal class YamlSecretsPluginTest {

    @Test
    fun `test if plugin was successfully applied and extension is available`() {
        // given
        val project: Project = ProjectBuilder.builder().build()

        // when
        project.pluginManager.apply(YamlSecretsPlugin::class.java)

        // then
        assertEquals(1, project.plugins.size)
        assertNotNull(project.extensions.getByName(YAML_SECRETS_PLUGIN_EXTENSION_NAME))
    }

}