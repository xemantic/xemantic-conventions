import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    `maven-publish`
    signing
    alias(libs.plugins.dokka)
    alias(libs.plugins.versions)
    alias(libs.plugins.jreleaser)
    alias(libs.plugins.power.assert)
    alias(libs.plugins.binary.compatibility.validator)
}

repositories {
    mavenCentral()
}

gradlePlugin {
    website = "https://github.com/xemantic/xemantic-gradle-plugin"
    vcsUrl = "https://github.com/xemantic/xemantic-gradle-plugin.git"
    plugins {
        create("xemantic-gradle-plugin") {
            id = "com.xemantic.gradle.xemantic-gradle-plugin"
            implementationClass = "com.xemantic.gradle.XemanticGradlePlugin"
        }
    }
}

val javaTarget = libs.versions.java.get()

kotlin {
    // set up according to https://jakewharton.com/gradle-toolchains-are-rarely-a-good-idea/
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(javaTarget)
        freeCompilerArgs.add("-Xjdk-release=$javaTarget")
    }
}

tasks {
    compileJava {
        options.release = javaTarget.toInt()
        targetCompatibility = javaTarget
        sourceCompatibility = javaTarget
    }
}

dependencies {
    testImplementation(libs.kotlin.test)
}
