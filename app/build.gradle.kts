import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

val localProperties = Properties().apply {
    load(File(rootProject.projectDir, "local.properties").reader())
}
val apiKey: String = localProperties.getProperty("apiKey", "")

android {
    namespace = "com.personal.healthy1"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.personal.healthy1"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_KEY", apiKey)
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
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.09.02")
    implementation(composeBom)

    implementation (libs.androidx.runtime)
    implementation (libs.androidx.ui)
    implementation (libs.androidx.foundation)
    implementation (libs.androidx.foundation.layout)
    implementation (libs.androidx.material3)
    implementation (libs.androidx.runtime.livedata)
    implementation (libs.androidx.ui.tooling)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.langchain4j)
    implementation(libs.langchain4j.open.ai)

    implementation(libs.tinylog.impl)
    implementation(libs.slf4j.tinylog)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation (libs.text.recognition)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(composeBom)
}