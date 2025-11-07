plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.copylearn"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.copylearn"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}


// --- ML Kit Text Recognition (enable only when requested) ---
// dependencies {
//     // On-device text recognition (Latin)
//     // Use the latest stable per docs if different:
//     // https://developers.google.com/ml-kit/vision/text-recognition/android
//     // implementation("com.google.mlkit:text-recognition:16.0.0")
//
//     // If you plan to use the suspend `.await()` helper (optional):
//     // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
// }
//
// android {
//     // No changes required here for now.
//     // If you target Java 17, ensure:
//     // compileOptions {
//     //     sourceCompatibility = JavaVersion.VERSION_17
//     //     targetCompatibility = JavaVersion.VERSION_17
//     // }
//     // kotlinOptions {
//     //     jvmTarget = "17"
//     // }
// }
