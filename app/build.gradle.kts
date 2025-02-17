import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.Projecte3MushTool"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.Projecte3MushTool"
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
        dataBinding = true
        viewBinding = true

    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}


dependencies {
    implementation("org.osmdroid:osmdroid-android:6.1.14")
    implementation("org.osmdroid:osmdroid-mapsforge:6.1.14")
    implementation("org.osmdroid:osmdroid-wms:6.1.14")
    implementation("org.osmdroid:osmdroid-geopackage:6.1.14")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("com.github.Gurupreet:FontAwesomeCompose:1.0.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.33.1-alpha")
    implementation("org.osmdroid:osmdroid-android:6.1.14")
    implementation("org.osmdroid:osmdroid-mapsforge:6.1.14")
    implementation("org.osmdroid:osmdroid-wms:6.1.14")
    implementation("org.osmdroid:osmdroid-geopackage:6.1.14")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("com.github.Gurupreet:FontAwesomeCompose:1.0.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.33.1-alpha")
    implementation(platform("androidx.compose:compose-bom:2023.06.00"))
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.camera:camera-camera2:1.1.0-alpha10")
    implementation("androidx.camera:camera-lifecycle:1.1.0-alpha10")
    implementation("androidx.camera:camera-view:1.0.0-alpha31")
    implementation("androidx.camera:camera-extensions:1.0.0-alpha31")
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.firebaseui:firebase-ui-auth:8.0.2")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:2.9.9")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.3")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation(platform("androidx.compose:compose-bom:2023.06.00"))
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.camera:camera-camera2:1.1.0-alpha10")
    implementation("androidx.camera:camera-lifecycle:1.1.0-alpha10")
    implementation("androidx.camera:camera-view:1.0.0-alpha31")
    implementation("androidx.camera:camera-extensions:1.0.0-alpha31")
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("io.coil-kt:coil-compose:1.4.0")
    implementation("org.osmdroid:osmdroid-android:6.1.14")
    implementation("org.osmdroid:osmdroid-mapsforge:6.1.14")
    implementation("org.osmdroid:osmdroid-wms:6.1.14")
    implementation("org.osmdroid:osmdroid-geopackage:6.1.14")


}

