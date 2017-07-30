## Petrichor - a face detection mobile application

雨息 - 人脸检测 使用了 Google 的原生人脸检测算法与交叉编译的中科视拓 (seeta) 人脸检测框架并提供了一套用于 Android 基于视频的实时人脸检测 API 接口。

接下来的版本将致力于改进 native 执行效率 (如 OpenCL 并行计算)，可能会加入 3D 人脸重建功能。

This project use google's face detection api and cross compiled seeta face detection on android on real-time face detection base on video frames. It's provide a common application structure of `android.hardware.camera` (old api) preview matter and a way to process frame in video. And draw face rectangle detection on a drawable view.

We may improve this project that some native process can be accelecrlate by opencl on gpu. Also 3-D face rebuild may be the next main funtion.


## Efficiency
Function|Time cost(average)
--------|:---------------:
Seeta   |≈1200ms
Google  |≈200ms

## For Android Studio 2.3

这个工程使用 Android Studio 3.0 Canary 8 编写并调试通过，若您想用于 Android Studio 2.3 或 IntellJ IDEA 2017.3 以及相近版本，想按照以下说明修改 Gradle 配置。

This project was built and passed debug on Android Studio 3.0 Canary 8. Follow the steps below to configure you gradle build profile if you wish to build this demo on Android Studio 2.3 or IntellJ IDEA 2107.3 and so on.

- /build.gradle

```gradle
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.3.3'
    }
}

allprojects {
    repositories {
        jcenter()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

- app/build.gradle

```gradle
apply plugin: 'com.android.application'

android {
    compileSdkVersion 25
    buildToolsVersion "25.3.1"
    defaultConfig {
        applicationId "work.petrichor.petrichor"
        minSdkVersion 19
        targetSdkVersion 25
        // more options
    }
    // more options
    jackOptions {
        enabled true
    }
    dexOptions {
        incremental true
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
        exclude group: 'com.android.support', module: 'support-annotations'
    })
    compile 'com.android.support:appcompat-v7:25.3.1'
    testCompile 'junit:junit:4.12'
    compile 'com.android.support.constraint:constraint-layout:1.0.2'
    compile files('libs/FaceDetection.jar')
}
```


