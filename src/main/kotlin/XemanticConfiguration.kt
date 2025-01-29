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

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

public abstract class XemanticConfiguration @Inject constructor(
    private val project: Project
) {

    private val now = LocalDateTime.now()

    public val githubActor: String? by project.properties
    public val githubToken: String? by project.properties
    public val signingKey: String? by project.properties
    public val signingPassword: String? by project.properties

    public var description: String? = null

    public var inceptionYear: Int? = null

    public var license: License? = null

    public var vendor: String = "Xemantic"

    public var vendorUrl: String = "https://xemantic.com"

    public val copyright: String
        get() =
            "© ${if (inceptionYear != now.year) "$inceptionYear-" else ""}${now.year} $vendor"

    public var gitHubAccount: String = "xemantic"

    public val buildTime: String
        get() = now
            .atZone(ZoneId.systemDefault())
            .withZoneSameInstant(ZoneOffset.UTC)
            .format(TIMESTAMP_FORMATTER)

    private val _developers = mutableListOf<Developer>()
    public val developers: List<Developer> = _developers

    public val authorIds: List<String> get() = developers.map { it.id }

    public val isReleaseBuild: Boolean = !(project.version as String).endsWith("-SNAPSHOT")

    public val releasePageUrl: String =
        "https://github.com/$gitHubAccount/${project.rootProject.name}/releases/tag/v${project.version}"

    public val homepageUrl: String = "https://github.com/$gitHubAccount/${project.rootProject.name}"

    public val documentationUrl: String = "https://github.com/$gitHubAccount/${project.rootProject.name}"

    public val stagingDeployDir: File = project.layout.buildDirectory.dir(
        "staging-deploy"
    ).get().asFile

    public fun developer(
        id: String,
        name: String,
        email: String
    ) {
        _developers += Developer(id, name, email)
    }

    private fun validateParameters() {
        requireNotNull(description) { "description must be set" }
        requireNotNull(inceptionYear) { "inceptionYear must be set" }
        requireNotNull(license) { "license must be set" }
        require(_developers.isNotEmpty()) { "at least one developer must be added" }
    }

    public fun validate() {
        try {
            validateParameters()
        } catch (e: IllegalArgumentException) {
            throw GradleException(
                "Remember to add xemantic { } section to your build.gradle.kts, " +
                        "and fill it with required parameters: ${e.message}"
            )
        }
    }

    public fun configurePom(publication: MavenPublication) {
        publication.xemanticPom(project)
    }

}

public data class Developer(
    val id: String,
    val name: String,
    val email: String
)

private val TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern(
    "uuuu-MM-dd'T'HH:mm:ss'Z'"
)
