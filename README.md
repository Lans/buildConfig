# Android Gradle Composing builds 管理三方依赖

## 1、Gradle Composing builds 是啥？

摘自 Gradle 文档：复合构建只是包含其他构建的构建. 在许多方面，复合构建类似于 Gradle 多项目构建，不同之处在于，它包括完整的 builds ，而不是包含单个 projects

- 组合通常独立开发的构建，例如，在应用程序使用的库中尝试错误修复时
- 将大型的多项目构建分解为更小，更孤立的块，可以根据需要独立或一起工作

## 2、Gradle Composing builds 怎么用

1、创建plugin module，这里我们命名为 buildPlugin

2、删除无用文件，留下build.gradle和main

3、修改build.gradle，粘贴如下代码

```groovy
buildscript {
    repositories {
//        google()
        mavenCentral()
    }
    dependencies {
        // 因为使用的 Kotlin 需要需要添加 Kotlin 插件
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10"
    }

}

apply {
    plugin 'kotlin'
    plugin 'java-gradle-plugin'
}

repositories {
//    google()
    mavenCentral()
}

dependencies {
    implementation gradleApi()
    implementation "org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10"
}


compileKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
compileTestKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

gradlePlugin {
    plugins {
        version {
            // 在 app 模块需要通过 id 引用这个插件
            id = 'com.lans.buildplugin'
            // 实现这个插件的类的路径
            implementationClass = 'com.lans.buildplugin.VersionPlugin'
        }
    }
}
```

4、创建VersionPlugin类

```kotlin
package com.lans.buildplugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class VersionPlugin : Plugin<Project> {
    override fun apply(p0: Project) {

    }
}
```

！！！注：package com.lans.buildplugin 一定要写、一定要写、一定要写，重要的事说三遍

5、新建BuildManager.kt或者DepManager.kt，名字随意取，定义你想要的变量

```kotlin
object BuildManager {
    const val applicationId = "com.lans.myplugin"
    const val compileSdkVersion = 33
    const val minSdkVersion = 21
    const val targetSdkVersion = 33
    const val versionCode = 1
    const val versionName = "1.0.0"
}
```

```kotlin
object DepManager {
    /**
     * 依赖版本
     */
    private const val core_ktx_version = "1.7.0"
    private const val appcompat_version = "1.5.1"
    private const val material_version = "1.6.1"
    private const val constraintlayout_version = "2.1.4"

    const val core = "androidx.core:core-ktx:$core_ktx_version"

    const val appcompat = "androidx.appcompat:appcompat:$appcompat_version"
    const val material = "com.google.android.material:material:$material_version"
    const val constraintlayout =
        "androidx.constraintlayout:constraintlayout:$constraintlayout_version"

}
```

6、在项目 **settings.gradle** 文件内添加 includeBuild("buildPlugin") ，不是include !!!

```groovy
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "buildConfig"
include ':app'
includeBuild("buildPlugin")
```

7、在app或module的 build.gradle 中使用 buildPlugin

```groovy
//这里使用了BuildManager和DepManager，需要导包
import com.lans.buildplugin.*

plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
  	//导入自定义插件
    id 'com.lans.buildplugin'
}

android {
    namespace 'com.lans.myplugin'
    compileSdk BuildManager.compileSdkVersion

    defaultConfig {
        applicationId BuildManager.applicationId
        minSdk BuildManager.minSdkVersion
        targetSdk BuildManager.targetSdkVersion
        versionCode BuildManager.versionCode
        versionName BuildManager.versionName

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = '1.8'
    }
}

dependencies {
    implementation DepManager.core
    implementation DepManager.appcompat
    implementation DepManager.material
    implementation DepManager.constraintlayout
}
```

## 总结

1.使用groovy ext的方式，无法跟踪依赖关系，可读性差，不便维护

2.使用Kotlin + BuildSrc，支持双向跟踪，依赖更新时会重新构建整个项目

3.使用Composing builds，支持单向跟踪，依赖更新时不会构建整个项目

>https://github.com/Lans/buildConfig
