plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.sm.uni.os"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.sm.uni.os"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    signingConfigs {
        create("release") {
            storeFile = file("./key.jks")
            storePassword = "987654321"
            keyAlias = "SM"
            keyPassword = "987654321"
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.majorVersion
    }
    packaging {
        resources {
            excludes += "/META-INF/**"
            excludes += "/kotlin/**"
            excludes += "/*.txt"
            excludes += "/*.bin"
        }
        dex {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation("com.highcapable.yukihookapi:api:1.1.11")
    ksp("com.highcapable.yukihookapi:ksp-xposed:1.1.11")
    implementation("androidx.core:core-ktx:1.10.1")
    compileOnly("de.robv.android.xposed:api:82")
    implementation("org.luckypray:DexKit:1.1.8")
}