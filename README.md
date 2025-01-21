# xemantic-gradle-plugin
Sets up standard gradle conventions for [Xemantic](https://github.com/xemantic)'s 

## Why?

Setting up a gradle project for a Kotlin multiplatform library can be hassle. There are so many repetitive pieces of configuration which are easy to mismanage and omit. There are also special workarounds required to publish such libraries to maven central. This plugin is attempting to centralize all of that.

## Usage

To you `lib.versions.toml` (located in the `gradle` dir) add:

```toml
[versions]

# your other versions ...
xemanticConventionsPlugin = "0.1-SNAPSHOT"

[libraries]
# your libraries ...

[plugins]
# your other plugins ...
xemantic-conventions = { id = "com.xemantic.gradle.xemantic-conventions", version.ref = "xemanticConventionsPlugin" }

```

Then, in your `build.gradle.kts`, you can specify:

```kotlin
plugins {
    alias(libs.plugins.kotlin.multiplatform) // or jvm
    alias(libs.plugins.kotlin.plugin.power.assert) // optional
    alias(libs.plugins.kotlinx.binary.compatibility.validator) // optional
    alias(libs.plugins.dokka)
    alias(libs.plugins.versions) // optional
    `maven-publish`
    signing
    alias(libs.plugins.jreleaser)
    alias(libs.plugins.xemantic.conventions)
}
```
