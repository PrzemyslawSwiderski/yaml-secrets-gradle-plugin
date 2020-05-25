package com.pswidersk.gradle.yamlsecrets

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property


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