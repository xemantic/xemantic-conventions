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

package com.xemantic.gradle

import org.gradle.api.publish.maven.MavenPom

fun MavenPom.setUpGitHubPomDetails(
    project: String,
    gitHubProject: String,
    xemantic: XemanticGradleExtension,
) {
    name.set(project)
    description.set(xemantic.description)
    url.set("https://github.com/${xemantic.gitHubAccount}/$gitHubProject")
    inceptionYear.set(xemantic.inceptionYear.toString())
    organization {
        name.set(xemantic.vendor)
        url.set(xemantic.vendorUrl)
    }
    licenses {
        license {
            name.set(xemantic.license?.id)
            url.set(xemantic.license?.url)
            distribution.set("repo")
        }
    }
    scm {
        url.set("https://github.com/${xemantic.gitHubAccount}/$gitHubProject")
        connection.set("scm:git:git:github.com/${xemantic.gitHubAccount}/$gitHubProject.git")
        developerConnection.set("scm:git:https://github.com/${xemantic.gitHubAccount}/$gitHubProject.git")
    }
    ciManagement {
        system.set("GitHub")
        url.set("https://github.com/${xemantic.gitHubAccount}/${gitHubProject}/actions")
    }
    issueManagement {
        system.set("GitHub")
        url.set("https://github.com/${xemantic.gitHubAccount}/${gitHubProject}/issues")
    }
//    developers { devSpec ->
//        xemantic.developers.forEach { dev ->
//            devSpec.developer {
//                it.id.set(dev.id)
//                it.name.set(dev.name)
//                it.email.set(dev.email)
//            }
//        }
//    }
}
