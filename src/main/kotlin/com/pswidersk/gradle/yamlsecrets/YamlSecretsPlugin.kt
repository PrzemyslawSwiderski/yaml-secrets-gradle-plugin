package com.pswidersk.gradle.yamlsecrets

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class YamlSecretsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create(YAML_SECRETS_PLUGIN_EXTENSION_NAME, YamlSecretsResolver::class.java)
        findSecretTemplateFiles(project).forEach { templateFile ->
            val secretFile = copyTemplateToSecretFile(templateFile)
            loadProperties(project, secretFile)
        }
        addSecretFilesToGitIgnore(project)
    }

    private fun findSecretTemplateFiles(project: Project): Sequence<File> {
        return project.rootDir.walk()
                .filter { !it.nameWithoutExtension.startsWith(HIDDEN_PREFIX) }
                .filter { file -> SECRET_EXTENSIONS.any { file.name.endsWith(it) } }
    }

    private fun copyTemplateToSecretFile(secretTemplateFile: File): File {
        val secretFile = secretTemplateFile.parentFile.resolve("$HIDDEN_PREFIX${secretTemplateFile.name}")
        return if (secretFile.exists())
            secretFile
        else
            secretTemplateFile.copyTo(secretFile)
    }

    private fun loadProperties(project: Project, targetFile: File) {
        val mapper = YAMLMapper()
        val secrets = mapper.readValue<Map<String, *>>(targetFile)
        // strip '.sec.yml' / '.sec.yaml' extensions and remove '.' prefix from file name
        val targetFileNameStripped = targetFile.name.trimStart(HIDDEN_PREFIX).substringBefore(PROPS_SEP)
        project.secrets.addSecrets(targetFileNameStripped, secrets)
    }

    private fun addSecretFilesToGitIgnore(target: Project) {
        val gitIgnoreFile = target.rootDir.resolve(".gitignore")
        val yamSecretsHeader = "### Yaml Secrets files ###"
        with(gitIgnoreFile) {
            if (!exists())
                createNewFile()
            if (readLines().none { it.contains(yamSecretsHeader) }) {
                appendText("${System.lineSeparator()}$yamSecretsHeader")
                SECRET_EXTENSIONS.forEach { appendText("${System.lineSeparator()}$HIDDEN_PREFIX*.$it") }
            }
        }

    }

}
