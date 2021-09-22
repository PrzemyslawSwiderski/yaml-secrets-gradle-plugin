package com.pswidersk.gradle.yamlsecrets

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import java.io.File


/**
 * Creates a [Property] to hold values of the given type.
 *
 * @param T the type of the property
 * @return the property
 */
internal inline fun <reified T : Any> ObjectFactory.property(): Property<T> =
    property(T::class.javaObjectType)

/**
 * Gets the [YamlSecretsResolver] that is installed on the project.
 */
internal val Project.secrets: YamlSecretsResolver
    get() = extensions.getByType(YamlSecretsResolver::class.java)


/**
 * Copy template file to a secret file which should be filled.
 */
internal fun getPropertiesFile(templateFile: File): File {
    val secretFile = templateFile.parentFile.resolve("$HIDDEN_PREFIX${templateFile.name}")
    return if (secretFile.exists())
        secretFile
    else
        templateFile.copyTo(secretFile)
}

/**
 * Load secret files by the given directories.
 */
internal fun loadSecretsByDirs(secretsResolver: YamlSecretsResolver, directories: Sequence<File>) {
    directories.forEach { dir ->
        val templateFiles = getSecretTemplatesInDir(dir)
        templateFiles.forEach { templateFile ->
            val propertiesFile = getPropertiesFile(templateFile)
            loadProperties(secretsResolver, templateFile, propertiesFile)
        }
    }
}

/**
 * Method returns all secret files by the given input dir.
 */
internal fun getSecretTemplatesInDir(inputDir: File) = inputDir.walk()
    .maxDepth(1)
    .filter { !it.nameWithoutExtension.startsWith(HIDDEN_PREFIX) }
    .filter { file -> SECRET_EXTENSIONS.any { file.name.endsWith(it) } }

internal fun loadProperties(secretsResolver: YamlSecretsResolver, templateFile: File, targetFile: File) {
    val properties = parseYamlProperties(targetFile)
    // strip '.sec.yml' / '.sec.yaml' extensions and remove '.' prefix from file name
    val secretName = targetFile.name.trimStart(HIDDEN_PREFIX).substringBefore(PROPS_SEP)
    secretsResolver.addSecrets(secretName, YamlSecretsData(secretName, templateFile, targetFile, properties))
}

internal fun parseYamlProperties(targetFile: File): Map<String, Any> = try {
    val mapper = YAMLMapper()
    mapper.readValue(targetFile)
} catch (exception: Exception) {
    throw IllegalStateException(
        "Exception occurred during parsing YAML file (file can not be empty): $targetFile",
        exception
    )
}

/**
 *  Converts properties strings to a System variables convention strings,
 *  e.g. parent.someProperty.child -> PARENT_SOMEPROPERTY_CHILD
 */
fun fromDotCaseToSnake(inputStr: String): String {
    return inputStr.uppercase().replace(".", "_")
}