package com.pswidersk.gradle.yamlsecrets

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import java.io.File
import javax.inject.Inject

open class YamlSecretsExtension @Inject constructor(objects: ObjectFactory) {

    val secretTemplates: ListProperty<File> = objects.listProperty(File::class.java)

    internal val properties: Map<String, Map<String, *>> = emptyMap()
}