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

- **JAR Manifest Configuration**: Populates the `Implementation-Title`, `Implementation-Version`, `Implementation-Vendor` and `Implementation-Vendor-Id` manifest attributes from the project metadata,
  and bundles the project's `LICENSE` file into `META-INF`
- **AI-Friendly Test Logging**: Test failures are logged in a structured, machine-readable format optimized for processing by coding AI agents (like Claude Code).
  Only failures and skipped tests are logged, each failure with its message, captured standard output and full stack trace,
  which reduces noise and makes CI/CD output easily digestible by both humans and AI tools.
- **Version Management**: The `updateVersionsAfterRelease` task rewrites the released version in `README.md` and sets the version in `gradle.properties` to the next snapshot
- **JReleaser Integration**: Announces releases on Discord, LinkedIn and Bluesky - the release itself is published by the [maven-publish](https://vanniktech.github.io/gradle-maven-publish-plugin/) plugin,
  JReleaser is used for announcements only

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
    alias(libs.plugins.version.catalog.update) // optional
    alias(libs.plugins.maven.publish) // com.vanniktech.maven.publish, provides the publishToMavenCentral task
    alias(libs.plugins.jreleaser)
    alias(libs.plugins.xemantic.conventions)
}

xemantic {
    description = "What this project is about"
    inceptionYear = "2025"
    applyAllConventions()
}
```

In a multimodule build apply the plugin to the root project only.
The conventions reach the subprojects on their own,
and `jreleaserAnnounce` waits for the `publishToMavenCentral` task of every module that has one,
so no aggregate task has to be declared by hand.

Dependency versions are not this plugin's concern -
the [version-catalog-update](https://github.com/littlerobots/version-catalog-update-plugin) plugin
already provides `pin`, `keep` and `versionSelector` idioms, so configure it directly in the project which needs it.

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
