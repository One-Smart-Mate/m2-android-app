pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

    }
}
//buildscript {
//    dependencies {
//       // classpath("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
//    }
//}
rootProject.name = "osm-android-app"
include(":app")
 