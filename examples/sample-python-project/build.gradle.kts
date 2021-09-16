import com.pswidersk.gradle.python.VenvTask

plugins {
    id("com.pswidersk.yaml-secrets-plugin") version "1.0.8"
    id("com.pswidersk.python-plugin") version "1.2.3"
}

pythonPlugin {
    pythonVersion.set("3.9.2")
    minicondaVersion.set("py38_4.8.3")
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