@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.xemantic.gradle.conventions.License
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jreleaser.model.Active

plugins {
    alias(libs.plugins.kotlin.dsl)
    `maven-publish`
    signing
    alias(libs.plugins.dokka)
    alias(libs.plugins.versions)
    alias(libs.plugins.jreleaser)
    alias(libs.plugins.power.assert)
    alias(libs.plugins.binary.compatibility.validator)
    alias(libs.plugins.xemantic.conventions)
}

group = "com.xemantic.gradle"

xemantic {
    description = "Sets up standard gradle conventions for Xemantic's projects"
    inceptionYear = 2025
    license = License.APACHE
    developer(
        id = "morisil",
        name = "Kazik Pogoda",
        email = "morisil@xemantic.com"
    )
}

val releaseAnnouncementSubject = """🚀 ${rootProject.name} $version has been released!"""

val releaseAnnouncement = """
$releaseAnnouncementSubject    

${xemantic.description}

${xemantic.releasePageUrl}
"""

gradlePlugin {
    website = "https://github.com/xemantic/xemantic-gradle-plugin"
    vcsUrl = "https://github.com/xemantic/xemantic-gradle-plugin.git"
    plugins {
        create("xemantic-conventions") {
            id = "$group.xemantic-conventions"
            implementationClass = "com.xemantic.gradle.conventions.XemanticConventionsPlugin"
            description = xemantic.description
        }
    }
}

repositories {
    mavenCentral()
}

val javaTarget = libs.versions.java.get()

kotlin {
    explicitApi()
    // set up according to https://jakewharton.com/gradle-toolchains-are-rarely-a-good-idea/
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(javaTarget)
        freeCompilerArgs.add("-Xjdk-release=$javaTarget")
    }
}

java {
    withSourcesJar()
}

tasks {

    withType<JavaCompile> {
        options.release.set(javaTarget.toInt())
        targetCompatibility = javaTarget
        sourceCompatibility = javaTarget
    }

    withType<Test> {
        useJUnitPlatform()
    }

}

dependencies {
    testImplementation(libs.kotlin.test)
    testImplementation(libs.xemantic.kotlin.test)
}

powerAssert {
    functions = listOf(
        "kotlin.assert",
        "com.xemantic.kotlin.test.assert",
        "com.xemantic.kotlin.test.have"
    )
}

// https://kotlinlang.org/docs/dokka-migration.html#adjust-configuration-options
dokka {
    pluginsConfiguration.html {
        footerMessage.set(xemantic.copyright)
    }
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.dokkaGeneratePublicationHtml)
}

publishing {
    publications {
        withType<MavenPublication> {
            artifact(javadocJar)
            xemantic.configurePom(this)
        }
    }
}

jreleaser {
    project {
        description = xemantic.description
        copyright = xemantic.copyright
        license = xemantic.license!!.spxdx
        links {
            homepage = xemantic.homepageUrl
            documentation = xemantic.documentationUrl
        }
        authors = xemantic.authorIds
    }
    deploy {
        maven {
            mavenCentral {
                create("maven-central") {
                    active = Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    applyMavenCentralRules = false
                    maxRetries = 240
                    stagingRepository(xemantic.stagingDeployDir.path)
                }
            }
        }
    }
    release {
        github {
            skipRelease = true // we are releasing through GitHub UI
            skipTag = true
            token = "empty"
            changelog {
                enabled = false
            }
        }
    }
    checksum {
        individual = false
        artifacts = false
        files = false
    }
    announce {
        webhooks {
            create("discord") {
                active = Active.ALWAYS
                message = releaseAnnouncement
                messageProperty = "content"
                structuredMessage = true
            }
        }
        linkedin {
            active = Active.ALWAYS
            subject = releaseAnnouncementSubject
            message = releaseAnnouncement
        }
        bluesky {
            active = Active.ALWAYS
            status = releaseAnnouncement
        }
    }
}
