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
import org.gradle.api.Project
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.Sign

internal fun Project.applyWorkarounds(
    xemantic: XemanticConfiguration
) {

    if (xemantic.isReleaseBuild) {
        // fixes https://github.com/jreleaser/jreleaser/issues/1292
        layout.buildDirectory.dir("jreleaser").get().asFile.mkdirs()
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

            tasks.named("jreleaserFullRelease").configure {
                mustRunAfter("publish")
            }

        }

    }

}
