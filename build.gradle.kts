plugins {
    id("com.android.application")
}

android {
    namespace = "com.mary.wellness"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mary.wellness"
        minSdk = 24
        targetSdk = 36
        versionCode = 4
        versionName = "1.3"
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.1")
}
