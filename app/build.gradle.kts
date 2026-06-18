plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")  // untuk Room annotation processing
}

android {
    namespace = "com.sipedas.app"          // sesuai package script kotlin kamu
    compileSdk = 36

    defaultConfig {
        applicationId = "com.hafizhhilman.sipedas"   // identitas unik di perangkat
        minSdk = 23
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

    // mencegah model TFLite terkompresi
    aaptOptions {
        noCompress("tflite")
    }

    buildFeatures {
        viewBinding = true
    }



}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.8.0")

    // TensorFlow Lite
    implementation("org.tensorflow:tensorflow-lite:2.14.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")

    // CameraX
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Firebase BoM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx")
    // Firebase Firestore
    implementation("com.google.firebase:firebase-firestore-ktx")
    // Google Play Services Auth (untuk Google Sign-In)
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Glide (untuk avatar)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("io.github.sceneview:sceneview:0.8.0")

    // Firebase BoM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx")
    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    implementation("androidx.gridlayout:gridlayout:1.0.0")

    implementation("com.github.PhilJay:MPAndroidChart:v3.0.3")

}