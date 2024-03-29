import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

// Для сборки нужно указать DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD, в local.properties
val databaseUrl: String = gradleLocalProperties(rootDir).getProperty("DATABASE_URL")
val databaseUser: String = gradleLocalProperties(rootDir).getProperty("DATABASE_USERNAME")
val databasePassword: String = gradleLocalProperties(rootDir).getProperty("DATABASE_PASSWORD")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    kotlin("kapt")
}

android {
    namespace = "aelsi2.natkschedule"
    compileSdk = 33

    defaultConfig {
        applicationId = "aelsi2.natkschedule"
        minSdk = 24
        targetSdk = 33
        versionCode = 3
        versionName = "0.3"

        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "DATABASE_URL", databaseUrl)
        buildConfigField("String", "DATABASE_USER", databaseUser)
        buildConfigField("String", "DATABASE_PASSWORD", databasePassword)
    }

    buildTypes {
        getByName("release") {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0"
    }
    flavorDimensions += listOf()
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val navVersion = "2.5.3"
    val roomVersion = "2.5.0"
    val composeBom = platform("androidx.compose:compose-bom:2023.05.01")

    //Новые стандартные библиотеки Java
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")

    //Расширения Kotlin
    implementation("androidx.core:core-ktx:1.10.1")

    //Жизненные циклы
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    //WorkManager
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    //Koin (автоматическое внедрение зависимостей)
    implementation("io.insert-koin:koin-android:3.3.3")
    implementation("io.insert-koin:koin-androidx-compose:3.4.2")

    //Room (хранение кэшированных расписаний)
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    //DataStore (хранение настроек)
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //JDBC MySQL connector (скачивание расписаний из сетевой БД)
    implementation("mysql:mysql-connector-java:5.1.49")

    //Compose (интерфейс)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(project(":aelsi2-compose"))

    //Compose navigation (навигация в интерфейсе)
    implementation("androidx.navigation:navigation-compose:$navVersion")

    //Splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    //Предпросмотр Compose
    debugImplementation("androidx.compose.ui:ui-tooling")

    //Тесты Compose
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    //Тесты
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
}

kapt {
    correctErrorTypes = true
}
