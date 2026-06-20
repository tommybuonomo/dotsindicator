// https://medium.com/@iRYO400/how-to-upload-your-android-library-to-maven-central-central-portal-in-2024-af7348742247

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.nmcp)
    alias(libs.plugins.roborazzi)
}

android {
    compileSdk = 37

    defaultConfig {
        minSdk = 23
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
    }
    namespace = "com.tbuonomo.viewpagerdotsindicator"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true   // Robolectric needs merged resources/manifest
            isReturnDefaultValues = true
            all { it.maxHeapSize = "2g" }       // native-graphics buffers for many PNGs
        }
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.dynamic.animation)
    implementation(libs.androidx.viewpager2)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    debugImplementation(libs.androidx.ui.tooling)

    // JVM Compose UI tests + Roborazzi golden screenshots (Robolectric, no emulator)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)
    debugImplementation(libs.androidx.ui.test.manifest) // MUST be debug: backs createComposeRule()
}
