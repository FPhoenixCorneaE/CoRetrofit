plugins {
    id(Deps.PluginIds.library)
    kotlin(Deps.PluginIds.kotlinAndroid)
    kotlin(Deps.PluginIds.kotlinKapt)
    `maven-publish`
}

android {
    compileSdk = Deps.Versions.compileSdkVersion
    buildToolsVersion = Deps.Versions.buildToolsVersion

    defaultConfig {
        minSdk = Deps.Versions.minSdkVersion
        targetSdk = Deps.Versions.targetSdkVersion

        consumerProguardFile("consumer-rules.pro")
    }

    buildTypes {
        getByName(Deps.BuildType.Release) {
            // 执行proguard混淆
            isMinifyEnabled = false
            // 移除无用的resource文件
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName(Deps.BuildType.Debug) {
            // 执行proguard混淆
            isMinifyEnabled = false
            // 移除无用的resource文件
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    sourceSets {
        val main by getting
        main.java.srcDirs("src/main/kotlin")
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_11
        sourceCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    lint {
        isCheckReleaseBuilds = false
        isAbortOnError = false
    }
}

dependencies {
    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    compileOnly(Deps.AndroidX.coreKtx)
    compileOnly(Deps.AndroidX.appcompat)
    compileOnly(Deps.Coroutines.core)
    compileOnly(Deps.Coroutines.android)
    compileOnly(Deps.FPhoenixCorneaE.common)
    api(project(mapOf("path" to ":retrofitUrlManager")))
    api(Deps.Retrofit2.retrofit)
    api(Deps.Retrofit2.converterGson)
    api(Deps.OkHttp3.loggingInterceptor)
    api(Deps.OkHttp3.persistentCookieJar)
}

// MavenPublication 配置 start -------------------------------------------------------------
afterEvaluate {
    publishing {
        publications {
            // Creates a Maven publication called "release".
            create<MavenPublication>(Deps.BuildType.Release) {
                from(components[Deps.BuildType.Release])
                groupId = "com.github.FPhoenixCorneaE"
                artifactId = project.name
                version = project.version.toString()
            }
        }
    }
}
// MavenPublication 配置 end ---------------------------------------------------------------