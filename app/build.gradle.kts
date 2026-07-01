import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val localProps = Properties().also { props ->
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { props.load(it) }
}

android {
    namespace = "com.bearings"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.bearings"
        minSdk = 34
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0"

        buildConfigField("String", "GOOGLE_OAUTH_CLIENT_ID",
            "\"${localProps.getProperty("GOOGLE_OAUTH_CLIENT_ID", "")}\"")
        buildConfigField("String", "ROUTES_API_KEY",
            "\"${localProps.getProperty("ROUTES_API_KEY", "")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.xr.runtime)
    implementation(libs.xr.compose)
    implementation(libs.xr.glimmer)
    implementation(libs.xr.projected)

    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.core.ktx)
}
