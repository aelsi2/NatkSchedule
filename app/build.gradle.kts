import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

// Для сборки нужно указать DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD, в local.properties
val databaseUrl: String = gradleLocalProperties(rootDir).getProperty("DATABASE_URL")
val databaseUser: String = gradleLocalProperties(rootDir).getProperty("DATABASE_USERNAME")
val databasePassword: String = gradleLocalProperties(rootDir).getProperty("DATABASE_PASSWORD")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
}

android {
    namespace = "aelsi2.natkschedule"
    compileSdk = 33

    defaultConfig {
        applicationId = "aelsi2.natkschedule"
        minSdk = 23
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "DATABASE_URL", databaseUrl)
        buildConfigField("String", "DATABASE_USER", databaseUser)
        buildConfigField("String", "DATABASE_PASSWORD", databasePassword)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val navVersion = "2.5.3"
    val roomVersion = "2.5.0"
    val composeBom = platform("androidx.compose:compose-bom:2023.01.00")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.2")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0")

    //Koin (автоматическое внедрение зависимостей)
    implementation("io.insert-koin:koin-android:3.3.3")
    implementation("io.insert-koin:koin-androidx-compose:3.4.2")

    //Room persistence (хранение кэшированных расписаний)
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    //DataStore (хранение настроек)
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //JDBC MySQL connector (скачивание расписаний из сетевой БД)
    implementation("mysql:mysql-connector-java:5.1.46")

    //Compose (интерфейс)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.6.1")

    implementation(project(":compose-material3-pullrefresh"))

    //Compose navigation (навигация в интерфейсе)
    implementation("androidx.navigation:navigation-compose:$navVersion")

    //Splash screen
    implementation("androidx.core:core-splashscreen:1.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

kapt {
    correctErrorTypes = true
}
