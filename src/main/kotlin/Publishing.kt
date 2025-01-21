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

import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication

public fun MavenPublication.xemanticPom(
    project: Project
) {
    pom {
        xemanticPomInPublication(project)
    }
}

private fun MavenPom.xemanticPomInPublication(
    project: Project
) {
    val xemantic = project.xemantic
    val gitHubAccount = xemantic.gitHubAccount
    val rootProjectName = project.rootProject.name
    val license = xemantic.license!!
    name.set(project.name)
    description.set(project.xemantic.description)
    url.set("https://github.com/$gitHubAccount/$rootProjectName")
    inceptionYear.set(xemantic.inceptionYear.toString())
    organization {
        name.set(xemantic.vendor)
        url.set(xemantic.vendorUrl)
    }
    licenses {
        license {
            name.set(license.name)
            url.set(license.url)
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
        xemantic.developers.forEach { dev ->
            developer {
                id.set(dev.id)
                name.set(dev.name)
                email.set(dev.email)
            }
        }
    }

}
