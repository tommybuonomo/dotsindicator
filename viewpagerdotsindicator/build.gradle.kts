// https://medium.com/@iRYO400/how-to-upload-your-android-library-to-maven-central-central-portal-in-2024-af7348742247

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.nmcp)
}

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    namespace = "com.tbuonomo.viewpagerdotsindicator"
}

kotlin {
    jvmToolchain(17)
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
}
