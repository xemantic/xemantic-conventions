@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    alias(libs.plugins.dokka)
    alias(libs.plugins.versions)
    alias(libs.plugins.jreleaser)
    alias(libs.plugins.power.assert)
    alias(libs.plugins.binary.compatibility.validator)
    alias(libs.plugins.maven.publish)
//    alias(libs.plugins.xemantic.conventions)
}

group = "com.xemantic.gradle"

fun MavenPomDeveloperSpec.projectDevs() {
    developer {
        id = "morisil"
        name = "Kazik Pogoda"
        url = "https://github.com/morisil"
    }
}

//xemantic {
//    description = "Sets up standard gradle conventions for Xemantic's projects"
//    inceptionYear = 2025
//    license = License.APACHE
//    developer(
//        id = "morisil",
//        name = "Kazik Pogoda",
//        email = "morisil@xemantic.com"
//    )
//}

//val releaseAnnouncementSubject = """🚀 ${rootProject.name} $version has been released!"""
//
//val releaseAnnouncement = """
//$releaseAnnouncementSubject
//
//${xemantic.description}
//
//${xemantic.releasePageUrl}
//"""

gradlePlugin {
    website = "https://github.com/xemantic/xemantic-gradle-plugin"
    vcsUrl = "https://github.com/xemantic/xemantic-gradle-plugin.git"
    plugins {
        create("xemantic-conventions") {
            id = "$group.xemantic-conventions"
            implementationClass = "com.xemantic.gradle.conventions.XemanticConventionsPlugin"
            //description = xemantic.description
            description = "Xemantic conventions plugin"
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

//java {
//    withSourcesJar()
//}

tasks.withType<JavaCompile> {
        options.release.set(javaTarget.toInt())
//        targetCompatibility = javaTarget
//        sourceCompatibility = javaTarget
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
        automaticRelease = true
    )

    // Fix for Gradle 9.2.0 task dependency validation with gradle-plugin-publish
    // The pluginMaven publication's signing task needs explicit dependency declaration
    tasks.withType<PublishToMavenRepository>().configureEach {
        dependsOn(tasks.withType<Sign>())
    }

    coordinates(
        groupId = group.toString(),
        artifactId = rootProject.name,
        version = version.toString()
    )

    pom {

        name = rootProject.name
        description = "Sets up standard gradle conventions for Xemantic's projects"
        inceptionYear = "2025"
        url = "https://github.com/xemantic/${rootProject.name}"

        organization {
            name = "Xemantic"
            url = "https://xemantic.com"
        }

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }

        scm {
            url = "https://github.com/xemantic/${rootProject.name}"
            connection = "scm:git:git://github.com/xemantic/${rootProject.name}.git"
            developerConnection = "scm:git:ssh://git@github.com/xemantic/${rootProject.name}.git"
        }

        ciManagement {
            system = "GitHub"
            url = "https://github.com/xemantic/${rootProject.name}/actions"
        }

        issueManagement {
            system = "GitHub"
            url = "https://github.com/xemantic/${rootProject.name}/issues"
        }

        developers {
            projectDevs()
        }

    }

}

// https://kotlinlang.org/docs/dokka-migration.html#adjust-configuration-options
//dokka {
//    pluginsConfiguration.html {
//        footerMessage.set(xemantic.copyright)
//    }
//}

//val javadocJar by tasks.registering(Jar::class) {
//    archiveClassifier.set("javadoc")
//    from(tasks.dokkaGeneratePublicationHtml)
//}

//jreleaser {
//    release {
//        github {
//            skipRelease = true // we are releasing through GitHub UI
//            skipTag = true
//            token = "empty"
//            changelog {
//                enabled = false
//            }
//        }
//    }
//    checksum {
//        individual = false
//        artifacts = false
//        files = false
//    }
//    announce {
//        webhooks {
//            create("discord") {
//                active = Active.ALWAYS
//                message = releaseAnnouncement
//                messageProperty = "content"
//                structuredMessage = true
//            }
//        }
//        linkedin {
//            active = Active.ALWAYS
//            subject = releaseAnnouncementSubject
//            message = releaseAnnouncement
//        }
//        bluesky {
//            active = Active.ALWAYS
//            status = releaseAnnouncement
//        }
//    }
//}

val unstableKeywords = listOf("alpha", "beta", "rc")

fun isNonStable(
    version: String
) = version.lowercase().let { normalizedVersion ->
    unstableKeywords.any {
        it in normalizedVersion
    }
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}
