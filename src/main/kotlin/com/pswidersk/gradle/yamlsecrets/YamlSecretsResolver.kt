package com.pswidersk.gradle.yamlsecrets

open class YamlSecretsResolver {
    private val secretsDataByName: MutableMap<String, YamlSecretsData> = mutableMapOf()

    inline fun <reified T> get(fullKey: String): T {
        val secretValue = getValue(fullKey)
        if (secretValue is T)
            return getValue(fullKey) as T
        else
            throw IllegalStateException("Illegal generic type: ${T::class.java.simpleName}," +
                    " secret value is type of: ${secretValue.javaClass.simpleName} for key: $fullKey")
    }

    inline fun <reified T> get(secretsName: String, yamlPropertyKey: String): T {
        val secretValue = getValue(secretsName, yamlPropertyKey)
        if (secretValue is T)
            return getValue(secretsName, yamlPropertyKey) as T
        else
            throw IllegalStateException("Illegal generic type: ${T::class.java.simpleName}," +
                    " secret value is type of: ${secretValue.javaClass.simpleName} for secret: $secretsName and key: $yamlPropertyKey")
    }

    fun getValue(fullKey: String): Any {
        return getValue(fullKey.substringBefore(PROPS_SEP), fullKey.substringAfter(PROPS_SEP, ""))
    }

    fun getValue(secretsName: String, yamlPropertyKey: String): Any {
        val secrets = secretsDataByName.getValue(secretsName)
        if (yamlPropertyKey == "")
            return secrets.properties
        return getNestedValue(yamlPropertyKey, secretsName, secrets.properties)
    }

    fun getSecretsData(secretsName: String): YamlSecretsData {
        return secretsDataByName.getValue(secretsName)
    }

    fun getNames(): Set<String> {
        return secretsDataByName.keys
    }

    private fun getNestedValue(yamlPropertyKey: String, secretsName: String, secretsMap: Map<*, *>): Any {
        val keys = yamlPropertyKey.split(PROPS_SEP)
        var currentMap = secretsMap
        keys.forEachIndexed { index, key ->
            when {
                isArrayIndex(key) -> {
                    val arrayIndex = extractArrayIndex(key)
                    when (val array = currentMap[keys[index - 1]]) {
                        is List<*> -> {
                            val arrayValue = array[arrayIndex]
                            when {
                                arrayValue == null -> throw IllegalStateException("Array value can not be null for key: $key and secrets: $secretsName.")
                                isLast(index, keys) -> return arrayValue
                                arrayValue is Map<*, *> -> currentMap = arrayValue
                            }
                        }
                        else -> throw IllegalStateException("Expecting array in key: $key and secrets: $secretsName.")
                    }
                }
                isLast(index, keys) -> {
                    val value = currentMap[key]
                    if (value == null)
                        throw IllegalStateException("Key: $key does not exists in secrets: $secretsName.")
                    else
                        return value
                }
                else -> {
                    when (val mapValue = currentMap[key]) {
                        null -> throw IllegalStateException("Key: $key does not exists in secrets: $secretsName.")
                        is Map<*, *> -> currentMap = mapValue
                    }
                }
            }
        }
        throw IllegalStateException("Key: $yamlPropertyKey is illegal in secrets: $secretsName.")
    }

    private fun isArrayIndex(key: String): Boolean {
        return key.startsWith(ARRAY_START) && key.endsWith(ARRAY_END)
    }

    private fun isLast(index: Int, keys: List<String>): Boolean {
        return index == keys.lastIndex
    }

    private fun extractArrayIndex(listIndex: String): Int {
        return try {
            listIndex.trimStart(ARRAY_START).trimEnd(ARRAY_END).toInt()
        } catch (t: Throwable) {
            throw IllegalStateException("Illegal array index: $listIndex, should match pattern " +
                    "'arrayProperty$PROPS_SEP${ARRAY_START}i$ARRAY_END', where i is an index value.")
        }
    }

    fun addSecrets(secretsFileName: String, yamlSecretsData: YamlSecretsData) {
        this.secretsDataByName[secretsFileName] = yamlSecretsData
    }

}