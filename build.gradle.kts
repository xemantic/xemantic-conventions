import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
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
        create("xemantic-conventions") {
            id = "com.xemantic.gradle.xemantic-conventions"
            implementationClass = "com.xemantic.gradle.conventions.XemanticGradlePlugin"
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

    withType<JavaCompile> {
        options.release.set(javaTarget.toInt())
        targetCompatibility = javaTarget
        sourceCompatibility = javaTarget
    }

    project.tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events(
                TestLogEvent.SKIPPED,
                TestLogEvent.FAILED
            )
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
        }
    }

}

dependencies {
    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
}
