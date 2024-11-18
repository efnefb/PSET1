plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.countingplugged"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.countingplugged"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packaging{
        resources{
            resources.excludes.add("META-INF/DEPENDENCIES")
        }
    }
    buildFeatures {
        mlModelBinding = true
        viewBinding = true
    }



}



dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.test.core)
    androidTestImplementation(libs.ext.junit)
    implementation(libs.pdfbox)
    implementation(libs.fontbox)
    implementation(libs.commons.logging)


    implementation("androidx.lifecycle:lifecycle-viewmodel:2.5.1")
    implementation("androidx.lifecycle:lifecycle-livedata:2.5.1")





}
