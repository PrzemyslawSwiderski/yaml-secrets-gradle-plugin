plugins {
    id("com.pswidersk.yaml-secrets-plugin") version "1.0.3"
}
val testProp = secrets.get<Int>("test.testProp2")
//println(testProp) // prints: 32
val secretNames = secrets.getNames()
//println(secretNames) // prints: [test, test2]
