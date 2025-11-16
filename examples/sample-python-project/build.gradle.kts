import com.pswidersk.gradle.python.VenvTask

plugins {
    id("com.pswidersk.yaml-secrets-plugin")
    id("com.pswidersk.python-plugin") version "3.0.0"
}

pythonPlugin {
    pythonVersion.set("3.9.2")
    condaVersion.set("25.9.1-0")
}

tasks {

    register<VenvTask>("runSamplePython") {
        workingDir.set(projectDir.resolve("main"))
        doFirst {
            val mainArgs = secrets.get<List<String>>("pythonSecrets.mainArgs")
            val envVars = secrets.get<Map<String, Any>>("pythonSecrets.envVars")
            environment = envVars
            args = listOf("main.py") + mainArgs
        }
    }

}