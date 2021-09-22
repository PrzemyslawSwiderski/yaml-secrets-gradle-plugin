import com.pswidersk.gradle.yamlsecrets.YamlSecretsData

plugins {
    id("com.pswidersk.yaml-secrets-plugin")
}
val testProp = secrets.get<Int>("test.testProp2")
check(testProp == 32)
val secretsNames = secrets.getNames()
check(secretsNames.containsAll(listOf("test2", "test")))

val expectedSecretsData = YamlSecretsData(
        "test2",
        file("test2.sec.yml"),
        file(".test2.sec.yml"),
        mapOf("testProp1" to "test2PropInNestedProject")
)
val secretsData = secrets.getSecretsData("test2")
check(secretsData == expectedSecretsData)
