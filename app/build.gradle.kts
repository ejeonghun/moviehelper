import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)

    // Supabase 설정
    kotlin("plugin.serialization") version "1.9.0"
}

android {
    namespace = "com.lunadev.moviehelper"
    compileSdk = 34
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    val properties = Properties().apply {
        load(FileInputStream("${rootDir}/local.properties"))
    }
    val supabase_url = properties["supabase_url"] ?: ""
    val supabase_key = properties["supabase_key"] ?: ""
    val kmdb_API_KEY = properties["kmdb_API_KEY"] ?: ""
    val kobis_API_KEY = properties["kobis_API_KEY"] ?: ""

    defaultConfig {
        applicationId = "com.lunadev.moviehelper"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "supabase_url", "\"$supabase_url\"")
        buildConfigField("String", "supabase_key", "\"$supabase_key\"")
        buildConfigField("String", "kmdb_API_KEY", "\"$kmdb_API_KEY\"")
        buildConfigField("String", "kobis_API_KEY", "\"$kobis_API_KEY\"")
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.livedata)
    implementation(libs.androidx.viewmodel)

    // coroutine
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation(libs.kotlinx.coroutines)

    // Retrofit 2
//    implementation("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.converter.gson)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.livedata)
    implementation(libs.androidx.viewmodel)

    implementation(libs.glide)


    // Supabase 의존성
    implementation(platform("io.github.jan-tennert.supabase:bom:2.4.3"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.ktor:ktor-client-android:2.3.11")
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.recyclerview)




    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

