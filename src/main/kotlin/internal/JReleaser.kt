/*
 * Copyright 2025 Kazimierz Pogoda / Xemantic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xemantic.gradle.conventions.internal

import com.xemantic.gradle.conventions.XemanticConfiguration
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.configure
import org.jreleaser.gradle.plugin.JReleaserExtension

private const val PUBLISH_TO_MAVEN_CENTRAL = "publishToMavenCentral"

/**
 * The `publishToMavenCentral` tasks of this project and of all its subprojects.
 *
 * The maven-publish plugin registers the task in whichever project it is applied to, which in a
 * multimodule build is typically the subprojects and not the root, where JReleaser lives.
 */
private fun Project.publishToMavenCentralTasks(): List<TaskProvider<Task>> = allprojects
    .filter { PUBLISH_TO_MAVEN_CENTRAL in it.tasks.names }
    .map { it.tasks.named(PUBLISH_TO_MAVEN_CENTRAL) }
    .ifEmpty {
        throw GradleException(
            "No '$PUBLISH_TO_MAVEN_CENTRAL' task found in project '$path' or in any of its " +
                    "subprojects, so there is nothing to announce. The task is registered by the " +
                    "com.vanniktech.maven.publish plugin, but only when Maven Central publishing " +
                    "is enabled - set the 'mavenCentralPublishing' or 'SONATYPE_HOST' Gradle " +
                    "property, the way the release workflow does."
        )
    }

internal fun Project.configureJReleaserConventions(
    config: XemanticConfiguration
) {

    tasks.named("jreleaserAnnounce") {
        // resolved lazily, after every project has been evaluated, instead of assuming a
        // `publishToMavenCentral` task in the project JReleaser itself is applied to
        dependsOn("build", provider { publishToMavenCentralTasks() })
    }

    // we are not releasing with jreleaser, just announcing
    // still we need default values here to pass validations
    extensions.configure<JReleaserExtension> {

        release {
            github {
                skipRelease.set(true)
                skipTag.set(true)
                token.set("empty")
                changelog {
                    enabled.set(false)
                }
            }
        }

        project {
            description.set(config.description)
            copyright.set(config.copyright)
            license.set("xemantic")
            links {
                homepage.set("https://example.com")
                documentation.set("https://example.com")
            }
            authors.set(listOf("xemantic"))
        }

    }

}
