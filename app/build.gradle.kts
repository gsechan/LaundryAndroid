plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    kotlin("plugin.serialization") version "2.3.21"
    jacoco
}

android {
    namespace = "com.gabesechan.laundrydemo"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.gabesechan.laundrydemo"
        minSdk = 28
        targetSdk = 36
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
    buildFeatures {
        compose = true
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
            shortUserHomeForRobolectric()?.let { shortHome ->
                all {
                    it.systemProperty("user.home", shortHome)
                }
            }
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.ui.test.junit4)
    implementation(libs.retrofit)
    implementation(libs.retrofitConverter)
    implementation(libs.hilt)
    implementation(libs.navigation)
    implementation(libs.androidx.core.splash)
    implementation(libs.androidx.datastore.preferences)
    ksp(libs.hilt.compiler) // Use `ksp` instead of `kapt`
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel-compose:1.3.0")
    implementation("com.googlecode.libphonenumber:libphonenumber:9.0.32")
    implementation("commons-validator:commons-validator:1.10.1")
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutineTest)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

jacoco {
    toolVersion = "0.8.12"
}

// Robolectric fails to resolve android-all jars from ~/.m2/repository when user.home
// contains a space (https://github.com/robolectric/robolectric/issues/5453). Work around
// it on Windows by pointing user.home at the 8.3 short path for the test JVM.
fun shortUserHomeForRobolectric(): String? {
    val home = System.getProperty("user.home")
    val isWindows = System.getProperty("os.name").startsWith("Windows")
    if (!isWindows || !home.contains(" ")) return null

    val process = ProcessBuilder("cmd", "/c", "for %I in (\"$home\") do @echo %~sI")
        .redirectErrorStream(true)
        .start()
    val shortPath = process.inputStream.bufferedReader().readText().trim()
    process.waitFor()
    return shortPath.takeIf { it.isNotEmpty() && !it.contains(" ") }
}

tasks.withType<Test> {
    extensions.configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    val fileFilter = listOf(
        "**/R.class", "**/R\$*.class", "**/BuildConfig.*", "**/Manifest*.*",
        "**/*Test*.*", "android/**/*.*", "**/*_Hilt*.*", "**/Hilt_*.*", "**/*_Factory.*"
    )
    val debugTree = fileTree("${layout.buildDirectory.get()}/intermediates/classes/debug/transformDebugClassesWithAsm/dirs") {
        exclude(fileFilter)
    }
    sourceDirectories.setFrom(files("$projectDir/src/main/java"))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(layout.buildDirectory.get()) {
        include("**/*.exec", "**/*.ec")
    })
}