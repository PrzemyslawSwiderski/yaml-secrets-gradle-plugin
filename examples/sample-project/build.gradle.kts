plugins {
    id("com.pswidersk.yaml-secrets-plugin")
}
val testProp = secrets.get<String>("test.testProp1")
check(testProp == "test")
val propInTest2 = secrets.get<String>("test", "testProp1")
check(propInTest2 == "test")
