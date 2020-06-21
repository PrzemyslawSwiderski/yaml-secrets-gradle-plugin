package com.pswidersk.gradle.yamlsecrets

import java.io.File

data class YamlSecretsData(val secretsName: String, val templateFile: File, val propertiesFile: File, val properties: Map<String, *>)