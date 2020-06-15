package com.pswidersk.gradle.yamlsecrets

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class YamlSecretsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create(YAML_SECRETS_PLUGIN_EXTENSION_NAME, YamlSecretsResolver::class.java)
        findSecretTemplateFiles(project).forEach { templateFile ->
            val secretFile = getProperSecretFile(templateFile)
            loadProperties(project.secrets, secretFile)
        }
        addSecretFilesToGitIgnore(project)
    }

    private fun findSecretTemplateFiles(project: Project): Sequence<File> {
        return generateSequence(project) { it.parent }.sorted().flatMap { p ->
            getSecretTemplatesInDir(p.projectDir)
        }
    }

    private fun getProperSecretFile(secretTemplateFile: File): File {
        val secretFile = secretTemplateFile.parentFile.resolve("$HIDDEN_PREFIX${secretTemplateFile.name}")
        return if (secretFile.exists())
            secretFile
        else
            secretTemplateFile.copyTo(secretFile)
    }

    private fun addSecretFilesToGitIgnore(target: Project) {
        val gitIgnoreFile = target.rootDir.resolve(".gitignore")
        if (gitIgnoreFile.exists()) {
            val yamSecretsHeader = "### Yaml Secrets files ###"
            with(gitIgnoreFile) {
                if (readLines().none { it.contains(yamSecretsHeader) }) {
                    appendText("${System.lineSeparator()}$yamSecretsHeader")
                    SECRET_EXTENSIONS.forEach {
                        appendText("${System.lineSeparator()}$HIDDEN_PREFIX*$it")
                    }
                }
            }
        }
    }

}
