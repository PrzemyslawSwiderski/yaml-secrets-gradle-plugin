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
 * Method returns all secret files by the given input dir.
 */
internal fun getSecretTemplatesInDir(inputDir: File) = inputDir.walk()
        .maxDepth(1)
        .filter { !it.nameWithoutExtension.startsWith(HIDDEN_PREFIX) }
        .filter { file -> SECRET_EXTENSIONS.any { file.name.endsWith(it) } }


internal fun loadProperties(secretsResolver: YamlSecretsResolver, targetFile: File) {
    val secrets = parseYamlSecrets(targetFile)
    // strip '.sec.yml' / '.sec.yaml' extensions and remove '.' prefix from file name
    val targetFileNameStripped = targetFile.name.trimStart(HIDDEN_PREFIX).substringBefore(PROPS_SEP)
    secretsResolver.addSecrets(targetFileNameStripped, secrets)
}

internal fun parseYamlSecrets(targetFile: File): Map<String, *> = try {
    val mapper = YAMLMapper()
    mapper.readValue(targetFile)
} catch (exception: Exception) {
    throw IllegalStateException("Exception occurred during parsing YAML file: $targetFile", exception)
}
