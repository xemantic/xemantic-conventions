# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Gradle plugin (`com.xemantic.gradle.xemantic-conventions`) that centralizes build conventions for Xemantic's Kotlin projects. It handles multiplatform library configuration, Maven Central publishing, signing, documentation generation, and release announcements.

## Build Commands

### Core build tasks
```bash
./gradlew build                    # Build and run tests
./gradlew test                     # Run tests only
./gradlew check                    # Run all checks including binary compatibility validation
```

### Running a single test
```bash
./gradlew test --tests "ClassName.testMethodName"
# Example: ./gradlew test --tests "UpdateVersionInReadmeTest.shouldReplaceGradleDependencyVersion"
```

### Documentation
```bash
./gradlew dokkaGeneratePublicationHtml    # Generate Dokka HTML documentation
```

### Publishing and Release
```bash
./gradlew publish                                    # Publish to staging or GitHub Packages
./gradlew -Pversion=X.Y.Z publish                    # Publish with specific version
./gradlew jreleaserFullRelease                       # Deploy to Maven Central and announce
./gradlew updateVersionInReadme                      # Update version in README.md
```

### Dependencies
```bash
./gradlew dependencyUpdates                          # Check for dependency updates (versions plugin)
```

## Architecture

### Plugin Structure

The plugin (`XemanticConventionsPlugin`) creates an `xemantic` extension (configured in `build.gradle.kts`) that requires:
- `description`: Project description
- `inceptionYear`: Year the project started
- `license`: One of `License.APACHE`, `License.GPL`, or `License.LGPL`
- At least one developer via `developer(id, name, email)`

Optional configuration:
- `vendor` (default: "Xemantic")
- `gitHubAccount` (default: "xemantic")

The plugin automatically configures:
1. **JAR Manifests** (`Jars.kt`): Populates implementation metadata, build time, license info
2. **Test Logging** (`TestLogging.kt`): Configured for AI-friendly output - only logs SKIPPED and FAILED tests with full stack traces
3. **Publishing** (`Publishing.kt`): Configures POM with organization, SCM, CI, issue management
4. **Signing**: Uses in-memory PGP keys from project properties
5. **Workarounds** (`Workarounds.kt`): Fixes for KMP signing issues and JReleaser task ordering

### Key Components

- **XemanticConfiguration.kt**: Main DSL configuration class, provides computed properties like `copyright`, `buildTime`, `releasePageUrl`, `isReleaseBuild`
- **Publishing.kt**: Maven POM configuration with GitHub/Maven Central metadata
- **UpdateVersionInReadme.kt**: Task to automatically update dependency versions in README.md
- **License.kt**: Enum of supported open-source licenses with SPDX identifiers

### Publishing Flow

The plugin supports two publishing modes:
1. **Snapshot builds** (`version` ends with `-SNAPSHOT`): Publish to GitHub Packages
2. **Release builds**: Publish to staging directory (`build/staging-deploy`), then JReleaser deploys to Maven Central

GitHub Actions workflow `build-release.yml` triggers on release publication and runs:
```bash
./gradlew -Pversion=$VERSION build sourcesJar signPluginMavenPublication publish jreleaserFullRelease
```

JReleaser is configured to:
- Deploy to Maven Central (via `maven-central` deployer)
- Announce to Discord, LinkedIn, and Bluesky
- Skip GitHub release creation (handled via GitHub UI)

### Required Secrets/Properties

For publishing to work:
- `githubActor` / `githubToken`: GitHub Packages authentication
- `signingKey` / `signingPassword`: PGP signing credentials
- `JRELEASER_*` environment variables: Maven Central and announcement platform credentials

### Test Configuration

Tests use JUnit Platform with:
- `kotlin-test` for basic assertions
- `xemantic-kotlin-test` for enhanced assertions
- `power-assert` plugin configured for `kotlin.assert`, `com.xemantic.kotlin.test.assert`, and `com.xemantic.kotlin.test.have`

Test output is optimized for AI agents (see `TestLogging.kt:27-29`): only failures and skips are logged to reduce noise.

### Binary Compatibility

The project uses `binary-compatibility-validator` plugin to ensure API stability. The `api` directory contains `.api` files tracking public API surfaces.

## Code Conventions

- Explicit API mode is enabled (`kotlin { explicitApi() }`)
- All public APIs must have visibility modifiers
- Internal implementation details go in `internal/` package
- JVM target is controlled by `libs.versions.toml` (`java` version)
- The plugin eats its own dog food: it applies itself in `build.gradle.kts:17`