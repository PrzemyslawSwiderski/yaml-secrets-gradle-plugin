# Gradle Yaml Secrets Plugin
This plugin loads properties from Yaml secret files.
Properties can later be used in build scripts.

## Requirements
* JRE 8 or higher

## How to use
1. Apply a plugin to a project as described on [gradle portal](https://plugins.gradle.org/plugin/com.pswidersk.yaml-secrets-plugin).
2. Add sample secrets template file in project directory. File must have `.sec.yml` or `.sec.yaml` extension, for example `testSecrets.sec.yml`:
```yaml
# obligatory value, must be fill in `.testSecrets.sec.yml` file
secretProp: <TO_BE_FILLED>
# property with default value
testProp2: 7
envVars: # fill envVars
args: # fill args list
```
3. Refresh project to create `.testSecrets.sec.yml` file.
4. Fill necessary properties in `.testSecrets.sec.yml` file.
    ```yaml
    secretProp: secretValue
    testProp2: 7
    envVars:
        ENV_VAR_1: testEnvVar
        ENV_VAR_2: 200
    args: 
        - 1
        - 2
        - 3
    ```
5. Query loaded properties by the following ways:
    ```kotlin
    (...)
    val secretProp = secrets.get<String>("testSecrets.secretProp")
    println(secretProp)
    // produces: "secretValue"
    
    // the same can be done by passing secret name as a separate parameter:
    val secretProp = secrets.get<String>("testSecrets", "secretProp")
    println(secretProp)
    // produces: "secretValue"
    
    val queriedValue = secrets.get<Int>("testSecrets.testProp2")
    println(queriedValue)
    // produces: "7"
    
    val queriedMap = secrets.get<Map<String,*>>("testSecrets.envVars")
    println(queriedMap)
    // produces: "{ENV_VAR_1=testEnvVar, ENV_VAR_2=200}"
    
    val queriedList = secrets.get<List<String>>("testSecrets.args")
    println(queriedList)
    // produces: "[1, 2, 3]"
    
    val queriedListElement = secrets.get<Int>("testSecrets.args[2]")
    println(queriedListElement)
    // produces: "3"
   
    val secretsData = secrets.getSecretsData("testSecrets")
    println(secretsData)
    // produces: "YamlSecretsData(secretsName=testSecrets, templateFile=<sample_path>\testSecrets.sec.yml, propertiesFile=<samplePath>\.testSecrets.sec.yml, properties={secretProp=secretValue (...)})"

    (...)
    ```

Check out `examples` directory to find sample projects.

## Notes
* plugin automatically adds secret files pattern to `.gitignore` if this file exists in root dir.
* secret files with the same name can be overridden in child projects (overriding single Yaml properties is not supported) 