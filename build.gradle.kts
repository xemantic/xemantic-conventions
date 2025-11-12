@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jreleaser.model.Active

plugins {
    `kotlin-dsl`
    alias(libs.plugins.dokka)
    alias(libs.plugins.versions)
    alias(libs.plugins.jreleaser)
    alias(libs.plugins.power.assert)
    alias(libs.plugins.binary.compatibility.validator)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.xemantic.conventions) // eating own dog food
}

group = "com.xemantic.gradle"

xemantic {
    description = "Gradle plugin setting up standard conventions for Xemantic's projects"
    inceptionYear = "2025"
    applyAllConventions()
}

fun MavenPomDeveloperSpec.projectDevs() {
    developer {
        id = "morisil"
        name = "Kazik Pogoda"
        url = "https://github.com/morisil"
    }
}

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
    gradlePluginPortal()
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

tasks.withType<JavaCompile> {
    options.release.set(javaTarget.toInt())
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.kotlin.semver)
    compileOnly(libs.verplugin)
    compileOnly(libs.jreleaser)
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

mavenPublishing {

    configure(
        KotlinJvm(
            javadocJar = JavadocJar.Dokka("dokkaGenerateHtml"),
            sourcesJar = true
        )
    )

    signAllPublications()

    publishToMavenCentral(
        automaticRelease = true,
        validateDeployment = false // sometimes it takes forever, we don't want to that build fails
    )

    coordinates(
        groupId = group.toString(),
        artifactId = rootProject.name,
        version = version.toString()
    )

    pom {

        name = rootProject.name
        description = xemantic.description
        inceptionYear = xemantic.inceptionYear.toString()
        url = "https://github.com/${xemantic.gitHubAccount}}/${rootProject.name}"

        organization {
            name = xemantic.organization
            url = xemantic.organizationUrl
        }

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }

        scm {
            url = "https://github.com/${xemantic.gitHubAccount}/${rootProject.name}"
            connection = "scm:git:git://github.com/${xemantic.gitHubAccount}/${rootProject.name}.git"
            developerConnection = "scm:git:ssh://git@github.com/${xemantic.gitHubAccount}/${rootProject.name}.git"
        }

        ciManagement {
            system = "GitHub"
            url = "https://github.com/${xemantic.gitHubAccount}/${rootProject.name}/actions"
        }

        issueManagement {
            system = "GitHub"
            url = "https://github.com/${xemantic.gitHubAccount}/${rootProject.name}/issues"
        }

        developers {
            projectDevs()
        }

    }

}

dokka {
    pluginsConfiguration.html {
        footerMessage.set(xemantic.copyright)
    }
}

val releaseAnnouncementSubject = """🚀 ${rootProject.name} $version has been released!"""

val releaseAnnouncement = """
$releaseAnnouncementSubject

${xemantic.description}

${xemantic.releasePageUrl}
"""

jreleaser {
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
