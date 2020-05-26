package com.pswidersk.gradle.yamlsecrets

open class YamlSecretsResolver {
    private val secretsByName: MutableMap<String, Map<String, *>> = mutableMapOf()

    inline fun <reified T> get(key: String): T {
        val secretValue = getValue(key)
        if (secretValue is T)
            return getValue(key) as T
        else
            throw IllegalStateException("Illegal generic type: ${T::class.java.simpleName}, secret value is type of: ${secretValue.javaClass.simpleName}")
    }

    fun getValue(key: String): Any {
        val secretsName = key.substringBefore(PROPS_SEP)
        val secretsMap = secretsByName.getValue(secretsName)
        val yamlPropertyKey = key.substringAfter(PROPS_SEP)
        if (yamlPropertyKey == "" || !key.contains(PROPS_SEP))
            return secretsMap
        return getNestedValue(yamlPropertyKey, secretsName, secretsMap)
    }

    private fun getNestedValue(fullKey: String, secretsName: String, secretsMap: Map<*, *>): Any {
        val keys = fullKey.split(PROPS_SEP)
        var currentMap = secretsMap
        keys.forEachIndexed { index, key ->
            when {
                isArrayIndex(key) -> {
                    val arrayIndex = extractArrayIndex(key)
                    when (val array = currentMap[keys[index - 1]]) {
                        is List<*> -> {
                            val arrayValue = array[arrayIndex]
                            when {
                                arrayValue == null -> throw IllegalStateException("Array value can not be null for key: $fullKey and secrets: $secretsName.")
                                isLast(index, keys) -> return arrayValue
                                arrayValue is Map<*, *> -> currentMap = arrayValue
                            }
                        }
                        else -> throw IllegalStateException("Expecting array in key: $fullKey and secrets: $secretsName.")
                    }
                }
                isLast(index, keys) -> {
                    val value = currentMap[key]
                    if (value == null)
                        throw IllegalStateException("Key: $fullKey does not exists in secrets: $secretsName.")
                    else
                        return value
                }
                else -> {
                    when (val mapValue = currentMap[key]) {
                        null -> throw IllegalStateException("Key: $fullKey does not exists in secrets: $secretsName.")
                        is Map<*, *> -> currentMap = mapValue
                    }
                }
            }
        }
        throw IllegalStateException("Key: $fullKey is illegal in secrets: $secretsName.")
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

    fun addSecrets(secretsFileName: String, secrets: Map<String, *>) {
        this.secretsByName[secretsFileName] = secrets
    }

}