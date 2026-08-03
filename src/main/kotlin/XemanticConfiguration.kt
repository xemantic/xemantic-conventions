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
import com.xemantic.gradle.conventions.internal.configureTestReporting
import com.xemantic.gradle.conventions.internal.populateJarManifest
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.testing.AbstractTestTask
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.withType
import java.time.LocalDate
import javax.inject.Inject

public abstract class XemanticConfiguration @Inject constructor(
    private val project: Project
) {

    public var description: String? = null

    public var inceptionYear: String? = null

    public var organization: String = "Xemantic"

    public var organizationUrl: String = "https://xemantic.com"

    public var gitHubAccount: String = "xemantic"

    /*
     * Everything derived from the properties above must be a computed property, never an
     * initializer. The extension is instantiated while the plugin is being applied, which is
     * before the build script's `xemantic { }` block assigns anything, and before a build
     * script assigning `version` has run. An initializer would capture the defaults - a still
     * null `inceptionYear` and an `unspecified` project version - and silently keep them,
     * accepting the `xemantic { }` overrides without effect. See issue #83.
     */

    public val url: String
        get() = "https://github.com/$gitHubAccount/${project.rootProject.name}"

    private var explicitCopyright: String? = null

    /**
     * The copyright notice, defaulting to `© <inceptionYear>-<currentYear> <organization>`,
     * collapsed to a single year when the project was started in the current one.
     *
     * The current year is taken at read time, so that a long-lived Gradle daemon does not
     * serve a stale notice across a New Year boundary.
     */
    public var copyright: String
        get() = explicitCopyright ?: LocalDate.now().year.toString().let { currentYear ->
            "© ${if (inceptionYear != currentYear) "$inceptionYear-" else ""}$currentYear $organization"
        }
        set(value) {
            explicitCopyright = value
        }

    public val isReleaseBuild: Boolean
        get() = !project.version.toString().endsWith("-SNAPSHOT")

    public val releasePageUrl: String
        get() = "https://github.com/$gitHubAccount/${project.rootProject.name}" +
                "/releases/tag/v${project.version}"

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

    public fun applyJarManifests() {
        project.allprojects {
            tasks.withType<Jar>().configureEach {
                populateJarManifest(this@XemanticConfiguration)
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

    public fun applyAllConventions() {
        applyJarManifests()
        applyAxTestReporting()
        applyJReleaserConventions()
    }

}
