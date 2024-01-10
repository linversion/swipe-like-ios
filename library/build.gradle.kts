plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.paparazzi)
  id("maven-publish")
}

kotlin {
  @Suppress("OPT_IN_USAGE")
  targetHierarchy.default()

  androidTarget()
  jvm()
  iosX64()
  iosArm64()
  iosSimulatorArm64()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(compose.ui)
        implementation(compose.foundation)
      }
    }

    val androidUnitTest by getting {
      dependencies {
        implementation(libs.junit)
        implementation(libs.compose.material3)
        implementation(libs.compose.materialIcons)
        implementation(libs.androidx.savedstate)
        implementation(libs.androidx.lifecycle)
      }
    }
  }
}

android {
  namespace = "com.linversion.swipe"

  defaultConfig {
    minSdk = libs.versions.minSdk.get().toInt()
    compileSdk = libs.versions.compileSdk.get().toInt()
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
  }
  java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
  }
  lint {
    abortOnError = true
  }

  buildTypes {
    release {
      isMinifyEnabled = false
    }
  }

  publishing {
    singleVariant("release") {
      withSourcesJar()
      withJavadocJar()
    }
  }
}

publishing {
  publications {
    register<MavenPublication>("release") {
      groupId = "com.linversion.swipe"
      artifactId = "swipe-like-ios"
      version = "1.0.0"

      afterEvaluate {
        from(components["release"])
      }
    }
  }
}
