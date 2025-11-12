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

package com.xemantic.gradle.conventions

import com.xemantic.gradle.conventions.internal.configureJReleaserConventions
import com.xemantic.gradle.conventions.internal.configureReportOnlyStableDependencyUpdates
import com.xemantic.gradle.conventions.internal.configureTestReporting
import com.xemantic.gradle.conventions.internal.populateJarManifest
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.testing.AbstractTestTask
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.Sign
import java.time.LocalDateTime
import javax.inject.Inject

public abstract class XemanticConfiguration @Inject constructor(
    private val project: Project
) {

    private val now = LocalDateTime.now()

    public var description: String? = null

    public var inceptionYear: String? = null

    public var organization: String = "Xemantic"

    public var organizationUrl: String = "https://xemantic.com"

    public var gitHubAccount: String = "xemantic"

    public val url: String = "https://github.com/$gitHubAccount/${project.rootProject.name}"

    public var copyright: String =
            "© ${if (inceptionYear != now.year.toString()) "$inceptionYear-" else ""}${now.year} $organization"

    public val isReleaseBuild: Boolean = !(project.version as String).endsWith("-SNAPSHOT")

    public val releasePageUrl: String =
        "https://github.com/$gitHubAccount/${project.rootProject.name}/releases/tag/v${project.version}"


    public var unstableVersionKeywords: List<String> = listOf("alpha", "beta", "rc")

    private fun validateParameters() {
        requireNotNull(description) { "description must be set" }
        requireNotNull(inceptionYear) { "inceptionYear must be set" }
    }

    internal fun validate() {
        try {
            validateParameters()
        } catch (e: IllegalArgumentException) {
            throw GradleException(
                "Remember to add xemantic { } section to your build.gradle.kts, " +
                        "and fill it with required parameters: ${e.message}"
            )
        }
    }

    public fun applySignBeforePublishing() {
        // Fix for Gradle 9.2.0 task dependency validation with gradle-plugin-publish
        // The pluginMaven publication's signing task needs explicit dependency declaration
        project.tasks.withType<PublishToMavenRepository>().configureEach {
            dependsOn(project.tasks.withType<Sign>())
        }
    }

    public fun applyJarManifests() {
        project.allprojects {
            tasks.withType<Jar> {
                populateJarManifest(project)
            }
        }
    }

    public fun applyAxTestReporting() {
        project.allprojects {
            tasks.withType<AbstractTestTask>().configureEach {
                configureTestReporting()
            }
        }
    }


    public fun applyJReleaserConventions() {
        project.pluginManager.withPlugin("org.jreleaser") {
            project.configureJReleaserConventions(
                config = this@XemanticConfiguration
            )
        }
    }

    public fun applyReportOnlyStableDependencyUpdates() {
        project.pluginManager.withPlugin(
            "com.github.benmanes.gradle.versions.updates"
        ) {
            project.configureReportOnlyStableDependencyUpdates(
                config = this@XemanticConfiguration
            )
        }
    }

    public fun applyAllConventions() {
        applySignBeforePublishing()
        applyJarManifests()
        applyAxTestReporting()
        applyJReleaserConventions()
        applyReportOnlyStableDependencyUpdates()
    }

}
