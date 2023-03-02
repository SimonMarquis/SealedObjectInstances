pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("com.gradle.enterprise") version "3.12.4"
    id("com.gradle.common-custom-user-data-gradle-plugin") version "1.8.2"
}

rootProject.name = "sealed-object-instances"
include("processor")
include("app")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        if (System.getenv("CI").toBoolean()) {
            publishAlways()
            isUploadInBackground = false
        }
    }
}
