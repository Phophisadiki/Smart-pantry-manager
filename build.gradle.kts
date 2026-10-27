// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}
dependencies{
    build.gradle: implementation 'androidx.recyclerview:recyclerview:1.3.0'
    build.gradle.kts: implementation("androidx.recyclerview:recyclerview:1.3.0")}

