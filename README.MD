<p>
  <a href="https://central.sonatype.com/artifact/io.github.utsmannn/compose-remote-layout/overview"><img alt="Maven Central Version" src="https://img.shields.io/maven-central/v/io.github.utsmannn/compose-remote-layout"></a>
  <a href="https://github.com/utsmannn/compose-remote-layout/actions/workflows/publish.yaml"><img alt="Publish to Maven Central" src="https://github.com/utsmannn/compose-remote-layout/actions/workflows/publish.yaml/badge.svg"></a>
</p>

# Compose Remote Layout

Article published
on [Medium](https://medium.com/@utsmannn/server-driven-ui-with-compose-remote-layout-bdc902d973f8)

## Background and Motivation

In modern app development, the ability to update UI components without deploying new app versions is
increasingly important. While solutions like React Native and Flutter offer this capability, they
often require learning new frameworks or languages. Compose Remote Layout bridges this gap for
Kotlin Multiplatform applications by enabling dynamic UI updates while staying within the Compose
ecosystem.

This library was created to solve several key challenges:

- The need for rapid UI iterations without app store submissions
- A/B testing requirements for UI components
- Platform-specific UI customization from a central source
- Real-time UI updates for critical changes

## Key Features

The `compose-remote-layout` module provides several core features:

1. **Component System**
    - Built-in support for basic Compose components (Column, Row, Box, Text, Button, Card)
    - All components can be defined and modified through JSON
    - Component properties map directly to Compose parameters

2. **Dynamic Updates**
    - Load layouts from remote sources (API, Firebase, local files)
    - Update UI without app redeployment
    - Handle layout changes in real-time

3. **Value Binding**
    - Dynamic text updates using the BindsValue system
    - Template-based value substitution
    - Real-time value changes support

4. **Modifier System**
    - Comprehensive modifier support matching Compose capabilities
    - Scoped modifiers for specific component types
    - JSON-based modifier definition

5. **Custom Components**
    - Register custom Composable functions
    - Map JSON definitions to custom UI elements
    - Pass custom parameters and handle specific logic

6. **iOS Support**
    - Independent iOS implementation using SwiftUI
    - Swift Package Manager integration

### Demo Video

https://github.com/user-attachments/assets/6479f0c1-338e-4d1e-90e9-25c2ebf72395

## Usage Guide

All documentation is available in the [docs](https://utsmannn.github.io/compose-remote-layout/)

## Download

Add the dependencies to your `build.gradle.kts`:

```kotlin
// Kotlin Multiplatform
implementation("io.github.utsmannn:compose-remote-layout:{version}")

// Choose platform-specific implementations as needed:
implementation("io.github.utsmannn:compose-remote-layout-android:{version}")
implementation("io.github.utsmannn:compose-remote-layout-jvm:{version}")
implementation("io.github.utsmannn:compose-remote-layout-iosx64:{version}")
implementation("io.github.utsmannn:compose-remote-layout-iosarm64:{version}")
implementation("io.github.utsmannn:compose-remote-layout-js:{version}")
```

### SPM Integration for iOS independent integration without Kotlin Multiplatform

For iOS projects without Kotlin Multiplatform, use Swift Package Manager:

Once you have your Swift package set up, adding ComposeRemoteLayoutSwift as a dependency is as easy
as adding it to the dependencies value of your Package.swift or the Package list in Xcode.

```swift
dependencies: [
    .package(url: "https://github.com/utsmannn/compose-remote-layout-swift.git", .upToNextMajor(from: "0.0.1-alpha05"))
]
```

Or add it via Xcode:

- Open your project in Xcode
- Go to File > Swift Packages > Add Package Dependency
- Enter the package URL: `https://github.com/utsmannn/compose-remote-layout-swift.git`
- Select the version you want to use (0.0.1-alpha05)
- Click Finish
- Add `import ComposeRemoteLayoutSwift` to your Swift files
- Build your project

See: [compose-remote-layout-swift](https://github.com/utsmannn/compose-remote-layout-swift)

## Important Disclaimer

⚠️ **Early Development Stage**

This library is currently in alpha stage (version {version}) and has several important
considerations:

1. **Production Use**: Not recommended for production applications at this time
2. **API Stability**: The API may change significantly between versions
3. **Performance**: Not fully optimized for large-scale applications
4. **Testing**: Limited real-world testing and validation
5. **Documentation**: May be incomplete or subject to changes

We recommend:

- Using this library for experimental projects
- Contributing feedback and bug reports
- Waiting for stable releases before production use
- Testing thoroughly in your specific use case

Please report issues and suggestions through the GitHub repository.