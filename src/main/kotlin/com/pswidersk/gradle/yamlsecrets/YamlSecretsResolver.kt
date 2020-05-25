package com.pswidersk.gradle.yamlsecrets

open class YamlSecretsResolver {
    val secretsByName: MutableMap<String, Map<String, *>> = mutableMapOf()

    fun getStringValue(key: String): String {
        val secretsName = key.substringBefore(PROPS_SEP)
        val secretsMap = secretsByName.getValue(secretsName)
        val yamlPropertyKey = key.substringAfter(PROPS_SEP)
        return getNestedStringValue(yamlPropertyKey, secretsName, secretsMap)
    }

    private fun getNestedStringValue(fullKey: String, secretsName: String, secretsMap: Map<*, *>): String {
        val keys = fullKey.split(PROPS_SEP)
        var value = secretsMap
        keys.forEachIndexed { index, key ->
            if (isArrayIndex(key)) {
                val arrayIndex = extractArrayIndex(key)
                when (val array = value[keys[index - 1]]) {
                    is List<*> -> {
                        when (val arrayValue = array[arrayIndex]) {
                            null -> throw IllegalStateException("Array value can not be null for key: $fullKey and secrets: $secretsName.")
                            is Map<*, *> -> value = arrayValue
                            is List<*> -> {
                            }
                            else -> if (index == keys.lastIndex) return arrayValue.toString()
                        }
                    }
                    else -> throw IllegalStateException("Expecting array in key: $fullKey and secrets: $secretsName.")
                }
            } else {
                when (val mapValue = value[key]) {
                    null -> throw IllegalStateException("Key: $fullKey does not exists in secrets: $secretsName.")
                    is Map<*, *> -> value = mapValue
                    is List<*> -> {
                    }
                    else -> return mapValue.toString()
                }
            }
        }
        throw IllegalStateException("Key: $fullKey is illegal in secrets: $secretsName.")
    }

    private fun isArrayIndex(key: String): Boolean {
        return key.startsWith(ARRAY_START) && key.endsWith(ARRAY_END)
    }

    private fun extractArrayIndex(listIndex: String): Int {
        return try {
            listIndex.trimStart(ARRAY_START).trimEnd(ARRAY_END).toInt()
        } catch (t: Throwable) {
            throw IllegalStateException("Illegal array index: $listIndex, should match pattern " +
                    "'arrayProperty$PROPS_SEP${ARRAY_START}i$ARRAY_END', where i is an index value.")
        }
    }

    fun addSecrets(secretsFileName: String, secrets: Map<String, *>) {
        this.secretsByName[secretsFileName] = secrets

    }

}