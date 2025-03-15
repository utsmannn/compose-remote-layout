# Getting Started with Compose Remote Layout

This guide will help you set up Compose Remote Layout in your project and understand its basic concepts.

## Installation

Add the dependencies to your project's `build.gradle.kts` file:

### For Kotlin Multiplatform Projects

```kotlin
// Add the core dependency
implementation("io.github.utsmannn:compose-remote-layout:{version}")

// Choose platform-specific implementations as needed:
implementation("io.github.utsmannn:compose-remote-layout-android:{version}")
implementation("io.github.utsmannn:compose-remote-layout-jvm:{version}")
implementation("io.github.utsmannn:compose-remote-layout-iosx64:{version}")
implementation("io.github.utsmannn:compose-remote-layout-iosarm64:{version}")
implementation("io.github.utsmannn:compose-remote-layout-js:{version}")
```

Replace `{version}` with the latest version (check the Maven Central badge on the [home page](./)).

### For Swift Projects (without Kotlin Multiplatform)

If you're using Swift without Kotlin Multiplatform, you can add the package using Swift Package Manager:

```swift
dependencies: [
    .package(url: "https://github.com/utsmannn/compose-remote-layout-swift.git", .upToNextMajor(from: "0.0.1-alpha05"))
]
```

Or add it via Xcode:

1. Open your project in Xcode
2. Go to File > Swift Packages > Add Package Dependency
3. Enter the package URL: `https://github.com/utsmannn/compose-remote-layout-swift.git`
4. Select the version you want to use
5. Click Finish
6. Import the package with `import ComposeRemoteLayoutSwift` in your Swift files