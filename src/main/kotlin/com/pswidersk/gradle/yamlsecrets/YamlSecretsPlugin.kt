package com.pswidersk.gradle.yamlsecrets

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class YamlSecretsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create(YAML_SECRETS_PLUGIN_EXTENSION_NAME, YamlSecretsExtension::class.java)
        project.afterEvaluate {
            val secretFiles = project.yamlSecrets.secretTemplates.get().map { secretsTemplateFile ->
                validateTemplateFile(secretsTemplateFile)
                val secretsFiles = copySecretsToHiddenFile(secretsTemplateFile)
                loadProperties(secretsFiles)
                secretsFiles
            }
            addSecretsFilesToGitIgnore(project, secretFiles)
        }
    }

    private fun validateTemplateFile(templateFile: File) {
        check(templateFile.exists()) { "Template File: ${templateFile.canonicalPath} must exist." }
        check(templateFile.isFile) { "Template File: ${templateFile.canonicalPath} is not regular file." }
        check(templateFile.extension in YAML_EXTENSIONS) { "Template File: ${templateFile.canonicalPath} must have one of extensions: $YAML_EXTENSIONS." }
    }

    private fun copySecretsToHiddenFile(secretTemplateFile: File): File {
        val secretFile = secretTemplateFile.parentFile.resolve(".${secretTemplateFile.name}")
        return if (secretFile.exists())
            secretFile
        else
            secretTemplateFile.copyTo(secretFile)
    }

    private fun addSecretsFilesToGitIgnore(target: Project, secretsFiles: List<File>) {
        val gitIgnoreFile = target.rootDir.resolve(".gitignore")
        val yamSecretsHeader = "### Yaml Secrets files ###"
        with(gitIgnoreFile) {
            if (!exists())
                createNewFile()
            if (readLines().none { it.contains(yamSecretsHeader) })
                appendText(System.lineSeparator() +
                        yamSecretsHeader +
                        System.lineSeparator())
            secretsFiles.forEach { secretFile ->
                val secretsFileRelativePath = secretFile.toRelativeString(target.rootDir).replace("\\", "/")
                if (readLines().none { it.contains(secretsFileRelativePath) }) {
                    appendText(secretsFileRelativePath +
                            System.lineSeparator())
                }
            }
        }

    }

    private fun loadProperties(targetFile: File) {
        val mapper = YAMLMapper()
        val secrets = mapper.readValue<Map<*, *>>(targetFile)
        println(secrets)
    }

}
