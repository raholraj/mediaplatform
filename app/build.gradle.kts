plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
}

val githubRepo: String = System.getenv("GITHUB_REPOSITORY")?.takeIf { it.isNotBlank() }
    ?: (project.findProperty("GITHUB_REPO") as String?)
    ?: ""

val adminEmail: String = System.getenv("ADMIN_EMAIL")?.takeIf { it.isNotBlank() }
    ?: (project.findProperty("ADMIN_EMAIL") as String?)
    ?: "admin@example.com"

android {
    namespace = "com.gugugaga.mediaplatform"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gugugaga.mediaplatform"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "0.4.0"
        buildConfigField("String", "GITHUB_REPO", "\"$githubRepo\"")
        buildConfigField("String", "ADMIN_EMAIL", "\"$adminEmail\"")
    }

    // Fixed debug keystore (CI decodes it from a secret) so APK updates keep the same signature.
    val fixedKeystore = file("debug.keystore")
    signingConfigs {
        if (fixedKeystore.exists()) {
            getByName("debug") {
                storeFile = fixedKeystore
                storePassword = System.getenv("DEBUG_KEYSTORE_PASSWORD") ?: "android"
                keyAlias = System.getenv("DEBUG_KEY_ALIAS") ?: "androiddebugkey"
                keyPassword = System.getenv("DEBUG_KEY_PASSWORD") ?: "android"
            }
        }
    }

    buildTypes {
        debug { isMinifyEnabled = false }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true; buildConfig = true }
    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.material)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.work.runtime)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.play)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)

    implementation(libs.androidx.webkit)
    implementation(libs.androidx.browser)
    implementation(libs.coil.compose)
    implementation(libs.androidx.biometric)
    implementation(libs.firebase.messaging)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
}
