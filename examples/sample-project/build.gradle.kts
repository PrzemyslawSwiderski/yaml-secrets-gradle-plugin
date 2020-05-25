plugins {
    id("com.pswidersk.yaml-secrets-plugin") version "1.0.0"
}
val testProp = secrets.getStringValue("test.testProp1")
println(testProp)
tasks {
    register("testSecrets") {
        doFirst {
            println(secrets.getStringValue("test.testProp1"))
            println(secrets.secretsByName)
        }
    }
}