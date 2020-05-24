plugins {
    id("com.pswidersk.yaml-secrets-plugin") version "1.0.0"
}

yamlSecrets {
    secretTemplates.set(listOf(
            file("testSecrets.yml"),
            file("testSecrets2.yml")
    ))
}