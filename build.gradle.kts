// Top-level build file where you can add configuration options common to all sub-projects/modules.
import java.util.Properties

val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    val properties = Properties()
    properties.load(localPropertiesFile.inputStream())
    properties.forEach { (key, value) ->
        project.extensions.extraProperties.set(key.toString(), value)
    }
}


plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false

}
