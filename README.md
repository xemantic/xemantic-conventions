# xemantic-gradle-plugin
Sets up standard gradle conventions for [Xemantic](https://github.com/xemantic)'s 

[<img alt="Maven Central Version" src="https://img.shields.io/maven-central/v/com.xemantic.gradle/xemantic-conventions">](https://central.sonatype.com/artifact/com.xemantic.gradle/xemantic-conventions)
[<img alt="GitHub Release Date" src="https://img.shields.io/github/release-date/xemantic/xemantic-conventions">](https://github.com/xemantic/xemantic-conventions/releases)
[<img alt="license" src="https://img.shields.io/github/license/xemantic/xemantic-conventions?color=blue">](https://github.com/xemantic/xemantic-conventions/blob/main/LICENSE)

[<img alt="GitHub Actions Workflow Status" src="https://img.shields.io/github/actions/workflow/status/xemantic/xemantic-conventions/build-main.yml">](https://github.com/xemantic/xemantic-conventions/actions/workflows/build-main.yml)
[<img alt="GitHub branch check runs" src="https://img.shields.io/github/check-runs/xemantic/xemantic-conventions/main">](https://github.com/xemantic/xemantic-conventions/actions/workflows/build-main.yml)
[<img alt="GitHub commits since latest release" src="https://img.shields.io/github/commits-since/xemantic/xemantic-conventions/latest">](https://github.com/xemantic/xemantic-conventions/commits/main/)
[<img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/xemantic/xemantic-conventions">](https://github.com/xemantic/xemantic-conventions/commits/main/)

[<img alt="GitHub contributors" src="https://img.shields.io/github/contributors/xemantic/xemantic-conventions">](https://github.com/xemantic/xemantic-conventions/graphs/contributors)
[<img alt="GitHub commit activity" src="https://img.shields.io/github/commit-activity/t/xemantic/xemantic-conventions">](https://github.com/xemantic/xemantic-conventions/commits/main/)
[<img alt="GitHub code size in bytes" src="https://img.shields.io/github/languages/code-size/xemantic/xemantic-conventions">]()
[<img alt="GitHub Created At" src="https://img.shields.io/github/created-at/xemantic/xemantic-conventions">](https://github.com/xemantic/xemantic-conventions/commits)
[<img alt="kotlin version" src="https://img.shields.io/badge/dynamic/toml?url=https%3A%2F%2Fraw.githubusercontent.com%2Fxemantic%2Fxemantic-conventions%2Fmain%2Fgradle%2Flibs.versions.toml&query=versions.kotlin&label=kotlin">](https://kotlinlang.org/docs/releases.html)
[<img alt="discord users online" src="https://img.shields.io/discord/811561179280965673">](https://discord.gg/vQktqqN2Vn)
[![Bluesky](https://img.shields.io/badge/Bluesky-0285FF?logo=bluesky&logoColor=fff)](https://bsky.app/profile/xemantic.com)

## Why?

Setting up a gradle project for a Kotlin multiplatform library can be hassle. There are so many repetitive pieces of configuration which are easy to mismanage and omit. There are also special workarounds required to publish such libraries to maven central. This plugin is attempting to centralize all of that.

## Usage

To you `lib.versions.toml` (located in the `gradle` dir) add:

```toml
[versions]

# your other versions ...
xemanticConventionsPlugin = "0.3.2"

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
