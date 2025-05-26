plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")

}

android {
    namespace = "com.example.jetpackcomposeevoluznsewingmachine"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.jetpackcomposeevoluznsewingmachine"
        minSdk = 24
        targetSdk = 35
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
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation (libs.androidx.navigation.compose)
    implementation ("com.github.mik3y:usb-serial-for-android:3.4.6")
    implementation("io.coil-kt:coil-compose:2.5.0") // or latest
    implementation ("androidx.compose.animation:animation")

    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Room core
    implementation ("androidx.room:room-runtime:2.7.0")

    // Room compiler for annotation processing (REQUIRED for kapt!)
    kapt ("androidx.room:room-compiler:2.7.0")

    // Kotlin Coroutine Support for Room
    implementation ("androidx.room:room-ktx:2.7.0")

    // ViewModel and LiveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    // Coroutine libraries
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    //adding viewModel dependencies
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    implementation ("androidx.compose.material3:material3:1.1.0")

    implementation ("androidx.appcompat:appcompat:1.6.1")



    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    implementation ("androidx.compose.compiler:compiler:1.4.3")




}