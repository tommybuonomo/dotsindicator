plugins {
    id("com.android.library")
    kotlin("android")
    id("com.vanniktech.maven.publish").version("0.19.0")
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 15
        targetSdk = 32
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    testImplementation("junit:junit:4.13.2")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
}