# xemantic-conventions
Gradle plugin setting up standard conventions for [Xemantic](https://github.com/xemantic)'s projects

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

## Features

- **JAR Manifest Configuration**: Automatically populates implementation metadata, build time, and license info
- **Maven Central Publishing**: Configures POM with organization, SCM, CI, and issue management metadata
- **PGP Signing**: Supports in-memory PGP keys from project properties
- **AI-Friendly Test Logging**: Test failures are logged in a structured, machine-readable format optimized for processing by coding AI agents (like Claude Code). Only failures and skipped tests are logged with full stack traces to reduce noise and make CI/CD output easily digestible by both humans and AI tools.
- **Version Management**: Automated task to update dependency versions in README.md
- **JReleaser Integration**: Seamless deployment to Maven Central with announcements to Discord, LinkedIn, and Bluesky

## Usage

To you `lib.versions.toml` (located in the `gradle` dir) add:

```toml
[versions]

# your other versions ...
xemanticConventionsPlugin = "0.6.8"

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

## Test configuration

The current test reporting is configured for AI-friendly output, in particular when used together with [xemantic-kotlin-test](https://github.com/xemantic/xemantic-kotlin-test) library, so that an autonomous AI agent can perform TDD in a feedback loop, with maximal information and minimal noise, preventing context rot.

Example error report when running gradle build in JVM:

```
> Task :jvmTest FAILED
<test-failure test="com.xemantic.kotlin.test.ProjectDocumentationTest.foo equals bar()" platform="jvm">
<message>
assert("foo" == "bar")
             |
             false
</message>
<stacktrace>
  at app//org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:38)
  at app//org.junit.jupiter.api.Assertions.fail(Assertions.java:138)
  at app//kotlin.test.junit5.JUnit5Asserter.fail(JUnitSupport.kt:56)
  at app//kotlin.test.Asserter.assertTrue(Assertions.kt:694)
  at app//kotlin.test.junit5.JUnit5Asserter.assertTrue(JUnitSupport.kt:30)
  at app//kotlin.test.Asserter.assertTrue(Assertions.kt:704)
  at app//kotlin.test.junit5.JUnit5Asserter.assertTrue(JUnitSupport.kt:30)
  at app//com.xemantic.kotlin.test.AssertionsKt.assert(Assertions.kt:32)
  at app//com.xemantic.kotlin.test.ProjectDocumentationTest.foo equals bar(ProjectDocumentationTest.kt:25)
  at java.base@24.0.2/java.lang.reflect.Method.invoke(Method.java:565)
  at java.base@24.0.2/java.util.ArrayList.forEach(ArrayList.java:1604)
  at java.base@24.0.2/java.util.ArrayList.forEach(ArrayList.java:1604)
</stacktrace>
</test-failure>
ProjectDocumentationTest[jvm] > foo equals bar()[jvm] FAILED
```
