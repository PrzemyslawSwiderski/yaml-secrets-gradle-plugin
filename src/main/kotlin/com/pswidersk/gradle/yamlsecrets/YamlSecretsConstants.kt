package com.pswidersk.gradle.yamlsecrets


/**
 * Directory where gradle specific files are stored.
 */
const val YAML_SECRETS_PLUGIN_EXTENSION_NAME = "secrets"

/**
 * Extensions of files which will be found as secrets.
 */
val SECRET_EXTENSIONS = listOf(".sec.yml", ".sec.yaml")

/**
 * Prefix of secrets file when hidden.
 */
const val HIDDEN_PREFIX = '.'

/**
 * Separator of properties in secret files.
 */
const val PROPS_SEP = '.'

/**
 * Char indicating start of an array in property key.
 */
const val ARRAY_START = '['

/**
 * Char indicating end of an array in property key.
 */
const val ARRAY_END = ']'