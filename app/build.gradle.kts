plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.smartmusicfirst"
    compileSdk = 34

    defaultConfig {
        manifestPlaceholders += mapOf()
        applicationId = "com.example.smartmusicfirst"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
//        manifestPlaceholders = [redirectSchemeName= "com.example.myspotifyapp", redirectHostName: "callback"]
//        manifestPlaceholders = mutableMapOf("redirectSchemeName" to "com.example.smartmusicfirst", "redirectHostName" to "callback")
        manifestPlaceholders["redirectSchemeName"] = "com.example.smartmusicfirst"
        manifestPlaceholders["redirectHostName"] = "callback"

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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


dependencies {
    // General
    implementation("androidx.core:core-ktx:1.13.1")

    // Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.8")
    implementation("androidx.compose.runtime:runtime-rxjava2:1.6.8")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))

    // Material
    implementation("androidx.compose.material3:material3")

    // lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.8.3")

    // spotify
    implementation(files("../libs/spotify-app-remote-release-0.8.0.aar"))
    implementation("com.spotify.android:auth:2.1.0")

    // Gson and Volley
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.android.volley:volley:1.2.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    // Google ML Kit and services
    implementation("com.google.mlkit:image-labeling:17.0.8")
    implementation("com.google.mlkit:translate:17.0.2")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // data store
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Other
    implementation("androidx.browser:browser:1.8.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("io.coil-kt:coil-compose:1.3.1")
    implementation("androidx.navigation:navigation-testing:2.7.7")
    implementation("androidx.test:rules:1.6.1")
    implementation("androidx.test:core:1.6.1")
    implementation("androidx.test:runner:1.6.1")


    testImplementation("junit:junit:4.13.2")
    testImplementation ("com.google.truth:truth:1.4.4")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.06.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("com.google.truth:truth:1.4.4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}