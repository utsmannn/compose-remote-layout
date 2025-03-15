Compose Remote Layout is available
on [Maven Central](https://central.sonatype.com/artifact/io.github.utsmannn/compose-remote-layout).
This guide will help you add it to your project.

## Adding Dependencies

Add the dependencies to your project's `build.gradle.kts` file according to your platform needs.

### For Kotlin Multiplatform Projects

```kotlin
// Core dependency - required for all projects
implementation("io.github.utsmannn:compose-remote-layout:$version")

// Choose platform-specific implementations as needed:
// For Android apps
implementation("io.github.utsmannn:compose-remote-layout-android:$version")

// For JVM desktop apps
implementation("io.github.utsmannn:compose-remote-layout-jvm:$version")

// For iOS projects through KMP
implementation("io.github.utsmannn:compose-remote-layout-iosx64:$version") // iOS Simulator x64
implementation("io.github.utsmannn:compose-remote-layout-iosarm64:$version") // iOS devices

// For JavaScript/Web applications
implementation("io.github.utsmannn:compose-remote-layout-js:$version")

// For navigation support
implementation("io.github.utsmannn:compose-remote-layout-router:$version")
```

Replace `$version` with the latest version. You can check:

- The Maven Central badge on the [home page](/)
-
The [Maven Central Repository page](https://central.sonatype.com/artifact/io.github.utsmannn/compose-remote-layout-router)
for the most current version
- The GitHub releases page for release notes

### For Swift Projects (without Kotlin Multiplatform)

If you're developing a native iOS application without using Kotlin Multiplatform, you can integrate
Compose Remote Layout through Swift Package Manager:

```swift
dependencies: [
    .package(url: "https://github.com/utsmannn/compose-remote-layout-swift.git", .upToNextMajor(from: "0.0.1-alpha05"))
]
```

#### Adding via Xcode UI:

1. Open your project in Xcode
2. Go to File > Swift Packages > Add Package Dependency
3. Enter the package URL: `https://github.com/utsmannn/compose-remote-layout-swift.git`
4. Select the version you want to use
5. Click Finish
6. Import the package with `import ComposeRemoteLayoutSwift` in your Swift files

## Verifying Installation

To verify the installation is correct, you can create a simple test component:

```kotlin
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.createLayoutComponent

// In your Composable function
val component = createLayoutComponent("""{"text": {"content": "Hello World!"}}""")
DynamicLayout(component = component)
```

If this renders a simple "Hello World!" text, your installation is successful.

## Next Steps

Now that you've set up Compose Remote Layout in your project, continue
to [Basic Setup](../02-basic-setup) to learn how to create your first dynamic
layout.