import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import com.android.build.gradle.BaseExtension as AndroidBaseExtension
import com.android.build.gradle.BasePlugin as AndroidBasePlugin

buildscript {
  repositories {
    google()
    mavenCentral()
    maven {
      url = java.net.URI.create("https://jitpack.io")
    }
  }
}

// Lists all plugins used throughout the project without applying them.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.compose.multiplatform) apply false
  alias(libs.plugins.paparazzi) apply false
  alias(libs.plugins.dokka) apply false
}

allprojects {
  plugins.withType<AndroidBasePlugin>().configureEach {
    configure<AndroidBaseExtension> {
      compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
      }
    }
  }
  tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_17)
    }
  }
}
