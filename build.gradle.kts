@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jreleaser.model.Active
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.apply

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

group = "com.xemantic.gradle"

val TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern(
    "uuuu-MM-dd'T'HH:mm:ss'Z'"
)

// transitional configuration object, after the release this project can use itself
class Xemantic {
    val vendor = "Xemantic"
    val vendorUrl = "https://xemantic.com"
    val releasePageUrl = "https://github.com/xemantic/xemantic-gradle-plugin/releases/tag/v$version"
    val description = "Sets up standard gradle conventions for Xemantic's projects"
    val copyright = "© 2025 Xemantic"
    val homepageUrl = "https://github.com/xemantic/xemantic-gradle-plugin"
    val documentationUrl = "https://github.com/xemantic/xemantic-gradle-plugin"
    val authorIds = listOf("morisil")
    val gitHubAccount = "xemantic"

    val stagingDeployDir: File = project.layout.buildDirectory.dir(
        "staging-deploy"
    ).get().asFile

    fun configurePom(publication: MavenPublication) {
        publication.apply {
            pom { xemanticPomInPublication(project) }
        }
    }

    val signingKey: String? by project
    val signingPassword: String? by project

    private val now = LocalDateTime.now()

    val buildTime: String get() = now
        .atZone(ZoneId.systemDefault())
        .withZoneSameInstant(ZoneOffset.UTC)
        .format(TIMESTAMP_FORMATTER)

    val isReleaseBuild: Boolean = (project.version as String).endsWith("-SNAPSHOT")
}

val xemantic = Xemantic()

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

tasks {

    withType<JavaCompile> {
        options.release.set(javaTarget.toInt())
        targetCompatibility = javaTarget
        sourceCompatibility = javaTarget
    }

    withType<Test> {
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
    testImplementation(libs.xemantic.kotlin.test)
}

powerAssert {
    functions = listOf(
        "kotlin.assert",
        "com.xemantic.kotlin.test.assert",
        "com.xemantic.kotlin.test.have"
    )
}


val releaseAnnouncement = """
🚀 ${rootProject.name} $version has been released!

${xemantic.releasePageUrl}

${xemantic.description}
"""

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
        license = "Apache-2.0"
        links {
            homepage = xemantic.homepageUrl
            documentation = xemantic.documentationUrl
        }
        authors.set(xemantic.authorIds)
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
        }
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
    }
}

signing {
    useInMemoryPgpKeys(
        xemantic.signingKey,
        xemantic.signingPassword
    )
    sign(publishing.publications)
}

private fun MavenPom.xemanticPomInPublication(
    project: Project
) {
    val gitHubAccount = xemantic.gitHubAccount
    val rootProjectName = project.rootProject.name
    name.set(project.name)
    description.set(xemantic.description)
    url.set("https://github.com/$gitHubAccount/$rootProjectName")
    inceptionYear.set(inceptionYear.toString())
    organization {
        name.set(xemantic.vendor)
        url.set(xemantic.vendorUrl)
    }
    licenses {
        license {
            name.set("The Apache Software License, Version 2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            distribution.set("repo")
        }
    }
    scm {
        url.set("https://github.com/$gitHubAccount/$rootProjectName")
        connection.set("scm:git:git:github.com/$gitHubAccount/$rootProjectName.git")
        developerConnection.set("scm:git:https://github.com/$gitHubAccount/$rootProjectName.git")
    }
    ciManagement {
        system.set("GitHub")
        url.set("https://github.com/$gitHubAccount/$rootProjectName/actions")
    }
    issueManagement {
        system.set("GitHub")
        url.set("https://github.com/$gitHubAccount/$rootProjectName/issues")
    }
    developers {
        developer {
            id = "morisil"
            name = "Kazik Pogoda"
            email = "morisil@xemantic.com"
        }
    }

}


private fun Jar.populateJarManifest(
    project: Project,
) {
    manifest {
        attributes.let {
            it["Implementation-Title"] = archiveBaseName.get()
            it["Implementation-Version"] = archiveVersion.get()
            it["Implementation-Vendor"] = xemantic.vendor
            it["Implementation-Vendor-Id"] = project.rootProject.name
            it["Created-By"] = "gradle"
            it["Build-Time"] = xemantic.buildTime
            it["License"] = "Apache-2.0"
            it["License-Name"] = "The Apache Software License, Version 2.0"
            it["License-URL"] = "http://www.apache.org/licenses/LICENSE-2.0.txt"
        }
    }
    metaInf {
        from(project.rootProject.rootDir) {
            include("LICENSE")
        }
    }
}


tasks.withType<Jar> {
    populateJarManifest(project)
}

private fun Test.xemanticTestLogging() {
    testLogging {
        events(
            TestLogEvent.SKIPPED,
            TestLogEvent.FAILED
        )
        showStackTraces = true
        exceptionFormat = TestExceptionFormat.FULL
    }
}

tasks.withType<Test> {
    xemanticTestLogging()
}


if (xemantic.isReleaseBuild) {
    // fixes https://github.com/jreleaser/jreleaser/issues/1292
    layout.buildDirectory.dir("jreleaser").get().asFile.mkdir()
    xemantic.stagingDeployDir.mkdirs()
}

allprojects {

    // workaround for KMP/gradle signing issue
    // https://github.com/gradle/gradle/issues/26091
    tasks.withType<PublishToMavenRepository> {
        dependsOn(tasks.withType<Sign>())
    }

    // Resolves issues with .asc task output of the sign task of native targets.
    // See: https://github.com/gradle/gradle/issues/26132
    // And: https://youtrack.jetbrains.com/issue/KT-46466
    tasks.withType<Sign>().configureEach {
        val pubName = name.removePrefix("sign").removeSuffix("Publication")

        // These tasks only exist for native targets, hence findByName() to avoid trying to find them for other targets

        // Task ':linkDebugTest<platform>' uses this output of task ':sign<platform>Publication' without declaring an explicit or implicit dependency
        tasks.findByName("linkDebugTest$pubName")?.let {
            mustRunAfter(it)
        }
        // Task ':compileTestKotlin<platform>' uses this output of task ':sign<platform>Publication' without declaring an explicit or implicit dependency
        tasks.findByName("compileTestKotlin$pubName")?.let {
            mustRunAfter(it)
        }
    }

    if (xemantic.isReleaseBuild) {

        tasks.named("jreleaserDeploy").configure {
            mustRunAfter("publish")
        }

        tasks.named("jreleaserAnnounce").configure {
            mustRunAfter("jreleaserDeploy")
        }

    }

}
