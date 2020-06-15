plugins {
    id("com.pswidersk.yaml-secrets-plugin") version "1.0.4"
}
val testProp = secrets.get<Int>("test.testProp2")
check(testProp == 32)
val secretNames = secrets.getNames()
check(secretNames.containsAll(listOf("test2", "test")))
