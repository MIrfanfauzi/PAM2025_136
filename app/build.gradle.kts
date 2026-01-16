plugins {
//    id("com.android.application")
//    id("org.jetbrains.kotlin.android")
//    id("kotlin-kapt")
//    id("androidx.navigation.safeargs.kotlin")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.androidx.navigation.safeargs)

    // Apply kapt by its ID without a version
    id("org.jetbrains.kotlin.kapt")

    alias(libs.plugins.google.devtools.ksp)
}


android {
    namespace = "com.example.rotibox"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.rotibox"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
    
    buildFeatures {
        compose = true
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    //untuk mengatur versi library secara konsisten
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Core Android
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Lifecycle & ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // CardView
    implementation("androidx.cardview:cardview:1.0.0")

    // Fragment KTX
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Navigation versi Compose (NavHost, composable, rememberNavController)
    implementation(libs.androidx.navigation.compose)

    // ViewModel untuk Compose (viewModel(), factory, dll)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // LiveData bisa dipakai di Compose (observeAsState())
    implementation(libs.androidx.compose.runtime.livedata)

    // Add this line
    implementation(libs.androidx.compose.material.icons.extended)

    // BCrypt for password hashing
    implementation("at.favre.lib:bcrypt:0.10.2")


    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    // This is the part that was failing:
    ksp(libs.androidx.room.compiler) // Change kapt to ksp

    // Coil for image loading in Compose
    implementation("io.coil-kt:coil-compose:2.5.0")

    implementation("com.itextpdf:itext7-core:7.2.5")

}