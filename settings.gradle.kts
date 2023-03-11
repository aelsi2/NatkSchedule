pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
// Для сборки нужно рядом с папкой проекта поместить папку https://github.com/Omico/androidx-compose-material3-pullrefresh
includeBuild("../androidx-compose-material3-pullrefresh") {
    dependencySubstitution {
        substitute(module("me.omico.lux:lux-androidx-compose-material3-pullrefresh")).using(project(":library"))
    }
}
rootProject.name = "Natk Schedule"
include(":app")
