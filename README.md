# Gradle Yaml Secrets Plugin
This plugin can load properties from Yaml secret files.
Those properties can then be used in build scripts.

## Requirements
* JRE supported by gradle 6.4 version (JRE 8 or higher)

## How to use
1. Apply a plugin to a project as described on [gradle portal](https://plugins.gradle.org/plugin/com.pswidersk.yaml-secrets-plugin).
2. Add sample secrets template file in project directory. File must have `.sec.yml` or `.sec.yaml` extension, for example `testSecrets.sec.yml`:
```yaml
# obligatory value, must be fill in `.testSecrets.sec.yml` file
secretProp: <TO_BE_FILLED>
# property with default value
testProp2: 7
envVars: # fill args
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
5. Get properties in build script by query string:
    ```kotlin
    (...)
    val secretProp = secrets.getValue("testSecrets.secretProp")
    println(secretProp)
    // produces: "secretValue"
    
    val queriedValue = secrets.getValue("testSecrets.testProp2")
    println(queriedValue)
    // produces: "7"
    
    val queriedMap = secrets.getValue("testSecrets.envVars")
    println(queriedMap)
    // produces: "{ENV_VAR_1=testEnvVar, ENV_VAR_2=200}"
    
    val queriedList = secrets.getValue("testSecrets.args")
    println(queriedList)
    // produces: "[1, 2, 3]"
    
    val queriedListElement = secrets.getValue("testSecrets.args[2]")
    println(queriedListElement)
    // produces: "3"
    (...)
    ```

Check `examples` directory to find sample projects.

## Notes
* plugin automatically adds secret files pattern to `.gitignore` if exists in root dir.
* secret files with the same name can be overridden in child projects (overriding single Yaml properties is not supported) 