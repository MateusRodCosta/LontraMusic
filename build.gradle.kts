// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.aboutlibraries.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    id("com.ncorti.ktfmt.gradle") version "0.27.0" apply false
}

dependencyAnalysis {
    issues {
        all {
            onAny {
                severity("fail")
            }
        }
    }
}
