plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

// The Gemini API key is read from local.properties (never committed).
// Add this line to local.properties:  GEMINI_API_KEY=your_key_here
val localProps = java.util.Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val geminiApiKey: String = localProps.getProperty("GEMINI_API_KEY") ?: ""

android {
    namespace = "com.griffith.habittracker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.griffith.habittracker"
        minSdk = 24
        targetSdk = 35
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
            buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
        }
        debug {
            buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
        }
    }

    // ENABLE BuildConfig GENERATION
    buildFeatures {
        compose = true
        buildConfig = true  // ADD THIS LINE
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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

    // Vico dependencies
    implementation (libs.lottie.compose) // lottiefiles
    implementation(libs.core)
    implementation(libs.compose)
    implementation(libs.compose.m3) // For Material 3 theme integration
    implementation (libs.coil.compose.v222)
    implementation (libs.generativeai) //Gemini dependency 0.9.0
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)  //GIF dependency
    implementation (libs.accompanist.pager)
    implementation (libs.androidx.navigation.compose)  //Navigation dependency
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.protolite.well.known.types)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.androidx.ui.test.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}