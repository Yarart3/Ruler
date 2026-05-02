import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.services) apply false
}

val localProperties = Properties().apply {
    val propertiesFile = rootProject.file("local.properties")
    if (propertiesFile.exists()) {
        propertiesFile.inputStream().use(::load)
    }
}

localProperties.getProperty("local.build.dir")
    ?.takeIf { it.isNotBlank() }
    ?.let(::file)
    ?.let { localBuildDir ->
        layout.buildDirectory.set(localBuildDir.resolve("root-build"))
        subprojects {
            layout.buildDirectory.set(localBuildDir.resolve(name))
        }
    }
