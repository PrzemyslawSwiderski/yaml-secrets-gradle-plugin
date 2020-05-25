plugins {
    id("com.pswidersk.yaml-secrets-plugin")
}
val testProp1 = secrets.getValue("test.testProp1")
val testProp2 = secrets.getValue("test.testProp2")
//println("Test props in nested project: $testProp1 and $testProp2")
