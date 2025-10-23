<h1 align="center">KFirebaseStorage</h1><br>

<div align="center">
<a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
<a href="https://android-arsenal.com/api?level=23" rel="nofollow">
    <img alt="API" src="https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat" style="max-width: 100%;">
</a>
<img src="https://img.shields.io/badge/Platform-Android-brightgreen.svg?logo=android" alt="Badge Android" />
  <img src="https://img.shields.io/badge/Platform-iOS%20%2F%20macOS-lightgrey.svg?logo=apple" alt="Badge iOS" />
  <!-- <img src="https://img.shields.io/badge/Platform-JVM-8A2BE2.svg?logo=openjdk" alt="Badge JVM" />
    <img src="https://img.shields.io/badge/Platform-WASM%20%2F%20JS-yellow.svg?logo=javascript" alt="Badge JS" /> -->
<a href="https://github.com/the-best-is-best/"><img alt="Profile" src="https://img.shields.io/badge/github-%23181717.svg?&style=for-the-badge&logo=github&logoColor=white" height="20"/></a>

</div>

KFirebaseStorage is a Kotlin Multiplatform library designed to streamline the integration of Firebase services in your mobile applications. With this library, developers can effortlessly initialize Firebase for both Android and iOS, enabling a unified and efficient development experience.

## Download

[![Maven Central](https://img.shields.io/maven-central/v/io.github.the-best-is-best/kfirebase-storage)](https://central.sonatype.com/artifact/io.github.the-best-is-best/kfirebase-storage)

KFirebaseStorage available on `mavenCentral()`.

```kotlin
implementation("io.github.the-best-is-best:kfirebase-storage:2.1.0")
```

## Version Compatibility

| KFirebaseCrashlytics Version | Firebase iOS SDK | Minimum iOS Version | Minimum Android Version |
|------------------------------|------------------|---------------------|-------------------------|
| 1.1.0                        | Firebase v11.x   | iOS 13+             | 21                      |
| 2.0.0                        | Firebase v12.x   | iOS 15+             | 23                      |

### androidMain

```kotlin
     AndroidKFirebaseCore.initialization(this)
```

### iOS (Using Swift Package Manager - SPM)

Make sure to add Firebase as a dependency using Swift Package Manager (SPM) instead of CocoaPods.

1. Open your Xcode project.
2. Navigate to `File > Add Packages...`.
3. Enter Firebase repository URL: `https://github.com/firebase/firebase-ios-sdk`
4. Choose `FirebaseCore` and add it to your project.

### iosApp

```swift
import Firebase

FirebaseApp.configure()
```

## How to use

```kotlin
 ElevatedButton(
            onClick = {
                scope.launch {
                    val image = FileKit.openFilePicker(type = FileKitType.Image)
                    if (image != null) {
                        val res = firebaseStorage.uploadFile(
                            "test/${image.name}",
                            image.readBytes(),
                        )
                        res.fold(
                            onSuccess = {
                                println("Upload successful: $it")
                            },
                            onFailure = {
                                println("Error: ${it.message}")
                            }
                        )
                    }
                }
            }
        ) {
            Text("Upload Image")
        }
        ElevatedButton(
            onClick = {
                scope.launch {
                    val res = firebaseStorage.getFileBytes("test/IMG_0002.jpeg")

                    res.fold(
                        onSuccess = {
                            val path = saveBytes(it, "IMG_0002.jpeg", "test")
                            try {
                                openFile(path)
                            } catch (e: Exception) {
                                println("Error opening file: ${e.message}")
                            }
                        },
                        onFailure = {
                            println("Error: ${it.message}")
                        }
                    )

                }
            }
        ) {
            Text("Download Image by bytes")
        }


        ElevatedButton(
            onClick = {
                scope.launch {
                    firebaseStorage.deleteFile(
                        "test/IMG_0002.jpeg",
                    )
                }
            }
        ) {
            Text("Delete Image")
        }



```

🛡 License
Licensed under the Apache 2.0 License.
