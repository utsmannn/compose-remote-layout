# Compose Remote Layout

**Server-Driven UI for Compose Multiplatform**

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.utsmannn/compose-remote-layout)
![Publish to Maven Central](https://github.com/utsmannn/compose-remote-layout/actions/workflows/publish.yaml/badge.svg)

## Overview

Compose Remote Layout is a powerful library that enables dynamic UI updates in Compose Multiplatform
applications through JSON-based layout definitions. It allows you to modify your UI without
redeploying your application, making it ideal for A/B testing, rapid iterations, and
platform-specific customizations.

## Why Use Compose Remote Layout?

In modern app development, the ability to update UI components without deploying new app versions is
becoming increasingly important. While solutions like React Native and Flutter offer this
capability, they often require learning new frameworks or languages. Compose Remote Layout bridges
this gap by enabling dynamic UI updates within the familiar Compose ecosystem.

## Key Features

### Component System

- Built-in support for standard Compose components (Column, Row, Box, Text, Button, Card)
- All components are definable and modifiable through JSON
- Component properties map directly to Compose parameters

### Dynamic Updates

- Load layouts from remote sources (API, Firebase, local files)
- Update UI without app redeployment
- Handle layout changes in real-time

### Value Binding

- Dynamic text updates using the BindsValue system
- Template-based value substitution with `{variable}` syntax
- Support for real-time value changes

### Modifier System

- Comprehensive modifier support matching Compose capabilities
- Scoped modifiers for specific component types
- JSON-based modifier definition for styling and layout

### Custom Components

- Register custom Composable functions with `CustomNodes`
- Map JSON definitions to custom UI elements
- Pass custom parameters and handle specific logic

### Cross-Platform

- Support for Kotlin Multiplatform projects
- Independent iOS implementation via Swift Package Manager
- Consistent experience across platforms

## Use Cases

### A/B Testing

Test different layouts with different user segments to determine which performs better without
deploying multiple app versions.

### Dynamic Content

Display seasonal UI changes, feature announcements, or promotional content without requiring app
updates.

### Platform Customization

Serve platform-specific layouts or device-specific optimizations from a central source.

### Rapid Iterations

Quickly fix UI issues, roll out new features, or test experimental designs without going through the
app store review process.

## Getting Started

Ready to incorporate dynamic UI in your Compose application? Check out
our [Getting Started guide](./getting-started) to begin your journey with Compose Remote Layout.

> **Note**: Compose Remote Layout is currently in alpha stage. While functional, the API may change
> between versions. For production use, thorough testing is recommended.