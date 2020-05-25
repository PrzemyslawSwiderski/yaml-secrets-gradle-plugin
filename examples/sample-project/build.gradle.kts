plugins {
    id("com.pswidersk.yaml-secrets-plugin") version "1.0.0"
}
val testProp = secrets.getValue("test.testProp1")
