plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.gcendon.stockmaster"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.gcendon.stockmaster"
        minSdk = 24
        targetSdk = 36
        //VERSION CODE: Incrementalo en 1 (siempre es un número entero)
        // Es el que usa Google Play para saber si esta app es más nueva que la anterior.
        versionCode = 9
        //VERSION NAME: Poné tu nuevo "Badge" de versión
        // Es el que ven los usuarios.
        versionName = "1.5.3"

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
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // 2. ViewModel y Compose Integration
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // 3. FIREBASE (Usaremos la 33.1.1 que es una roca de estable)
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")
    // Para el login moderno (Credential Manager)
    implementation("androidx.credentials:credentials:1.2.2")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Firebase Auth
    implementation("com.google.firebase:firebase-auth-ktx")

    //iconos
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material:material-icons-extended:1.6.0")

    //navegacion
    implementation("androidx.navigation:navigation-compose:2.8.0")

    //cargar imagenes
    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
}