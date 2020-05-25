import com.pswidersk.gradle.python.VenvTask

plugins {
    id("com.pswidersk.yaml-secrets-plugin") version "1.0.0"
    id("com.pswidersk.python-plugin") version "1.1.2"
}

pythonPlugin {
    pythonVersion.set("3.8.2")
}

tasks {

    register<VenvTask>("runSamplePython") {
        workingDir(projectDir.resolve("main"))
        args(listOf("main.py"))
        doFirst {
            val args = secrets.getValue("pythonSecrets.mainArgs")
            val envVars = secrets.getValue("pythonSecrets.envVars") as Map<String, *>
            args(args)
            environment(envVars)
        }
    }

}