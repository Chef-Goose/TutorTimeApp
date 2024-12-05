pluginManagement {
    repositories {
        google {
            content {
                // Includes specific groups for Android-related dependencies
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // Enforce repository management at the settings level
    repositories {
        google()  // Only declare repositories here, not in project-level build.gradle.kts
        mavenCentral()
    }
}

rootProject.name = "TutorApp"
include(":app")
