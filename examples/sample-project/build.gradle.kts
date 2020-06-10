plugins {
    id("com.pswidersk.yaml-secrets-plugin") version "1.0.3"
}
val testProp = secrets.get<String>("test.testProp1")
