import com.pswidersk.gradle.python.VenvTask

plugins {
    id("com.pswidersk.yaml-secrets-plugin") version "1.0.7"
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
            val args = secrets.get<List<String>>("pythonSecrets.mainArgs")
            val envVars = secrets.get<Map<String, *>>("pythonSecrets.envVars")
            args(args)
            environment(envVars)
        }
    }

}