plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.niaz.diary"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.niaz.diary"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "1.3"

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
    buildFeatures {
        compose = true
    }
}

dependencies {
    //alias(libs.plugins.kotlin.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:2.6.1")

    // line below to avoid "AppDatabase_Impl does not exist"
    kapt("androidx.room:room-compiler:2.6.1")

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // ViewModel with Hilt
    implementation(libs.hilt.navigation.compose)

    // -------------------------TESTS

//    // JUnit
//    testImplementation("junit:junit:4.13.2")
//
//    // AndroidX Test
//    testImplementation("androidx.test:core:1.6.0")
//    testImplementation("androidx.test.ext:junit:1.2.0")
//    testImplementation("androidx.arch.core:core-testing:2.3.0")
//
//    // Coroutines Test
//    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
//
//    // MockK for Kotlin mocking
//    testImplementation("io.mockk:mockk:1.13.9")
//
//    // to test Compose UI (for other tests beyond this ViewModel test)
//    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.2")
//    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.2")

}