plugins {
    id("com.pswidersk.yaml-secrets-plugin")
}
val testProp1 = secrets.get<String>("test.testProp1")
check(testProp1 == "testPropOverridden")
val testProp2 = secrets.get<Int>("test.testProp2")
check(testProp2 == 1)