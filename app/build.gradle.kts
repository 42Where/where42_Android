plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//    kotlin("kapt") version "1.5.30"

}

android {
    namespace = "com.example.where42android"
    compileSdk = 34

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }



    defaultConfig {
        applicationId = "com.example.where42android"
        minSdk = 30 //31
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {


    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.ssomai:android.scalablelayout:2.1.6")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")



//    livedata
//    val lifecycle_version = "2.6.2"
//    val arch_version = "2.2.0"
//    // ViewModel
//    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
//    // ViewModel utilities for Compose
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
//    // LiveData
//    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
//    // Lifecycles only (without ViewModel or LiveData)
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
//    // Lifecycle utilities for Compose
//    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycle_version")
//    // Saved state module for ViewModel
//    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version")
//    // Annotation processor
//    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")
//    // alternately - if using Java8, use the following instead of lifecycle-compiler
//    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycle_version")
//    // optional - helpers for implementing LifecycleOwner in a Service
//    implementation("androidx.lifecycle:lifecycle-service:$lifecycle_version")
//    // optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
//    implementation("androidx.lifecycle:lifecycle-process:$lifecycle_version")
//    // optional - ReactiveStreams support for LiveData
//    implementation("androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycle_version")
//    // optional - Test helpers for LiveData
//    testImplementation("androidx.arch.core:core-testing:$arch_version")
//    // optional - Test helpers for Lifecycle runtime
//    testImplementation ("androidx.lifecycle:lifecycle-runtime-testing:$lifecycle_version")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    implementation ("androidx.lifecycle:lifecycle-common-java8:2.4.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0") // 최신 버전으로 업데이트 필요
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0") // LiveData에 대한 Kotlin 확장 라이브러리도 추가하는 것이 좋습니다.


    //리사이클러뷰
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    // For control over item selection of both touch and mouse driven selection
    implementation ("androidx.recyclerview:recyclerview-selection:1.1.0")


    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")


}