plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")  // Change this line
}

android {
    namespace = "com.cs407.readify"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cs407.readify"
        minSdk = 33
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
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            // Excludes the duplicate CONTRIBUTORS.md file
            excludes += "/META-INF/CONTRIBUTORS.md"
            excludes += "/META-INF/LICENSE.md"
            // If you encounter similar issues with other META-INF files, you can use:
            // excludes += ['META-INF/*']
        }
    }
}

dependencies {
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.navigation.fragment.ktx)

    implementation(libs.material)
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation ("com.google.android.material:material:1.6.0")
    implementation ("androidx.localbroadcastmanager:localbroadcastmanager:1.0.0")
    implementation ("com.atilika.kuromoji:kuromoji-ipadic:0.9.0")

    //ROOM
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    implementation(libs.vision.common)
    kapt("androidx.room:room-compiler:${libs.versions.roomCommon.get()}")  // Add this line



    implementation(libs.androidx.appcompat)

    // CameraX
    implementation ("androidx.camera:camera-core:1.3.0")
    implementation ("androidx.camera:camera-camera2:1.3.0")
    implementation ("androidx.camera:camera-lifecycle:1.3.0")
    implementation ("androidx.camera:camera-view:1.3.0")
    implementation ("androidx.camera:camera-extensions:1.3.0")


    implementation("com.google.mlkit:language-id:17.0.1")

    // ML Kit Text Recognition
    implementation("com.google.mlkit:text-recognition:16.0.0")
    implementation ("com.google.mlkit:text-recognition-japanese:16.0.0")
    // ML Kit Translation
    implementation("com.google.mlkit:translate:16.1.2")


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

   // ksp("androidx.room:room-compiler:2.5.0") // KSP for Room


}

