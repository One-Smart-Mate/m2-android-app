plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    alias(libs.plugins.hilt)
}

android {

    namespace = "com.ih.m2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ih.m2"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    kapt {
        correctErrorTypes = true
    }
    hilt {
        enableAggregatingTask = true
    }
}

dependencies {

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

    implementation(libs.hilt)
    kapt (libs.hilt.compiler)

    //Dagger Hilt Library
//    implementation(libs.hilt)
//    kapt(libs.hilt.compiler)
//    implementation(libs.android.hilt)
//    kapt(libs.android.hilt.compiler)
//    implementation(libs.retrofit.adapter)
//    implementation(libs.hilt.navigation.compose)

    //Retrofti
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.retrofit.adapter)
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation ("com.airbnb.android:mavericks:3.0.9")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("com.airbnb.android:mavericks-compose:3.0.9")
    implementation("com.airbnb.android:mavericks-hilt:3.0.8")

    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation(libs.hilt.navigation.compose)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.2")
    kapt ("androidx.lifecycle:lifecycle-compiler:2.8.2")



    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")


}