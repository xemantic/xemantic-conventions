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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class XemanticGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val now = LocalDateTime.now() //.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val extension = project.extensions.create(
            "xemantic",
            XemanticGradleExtension::class.java,
            now.year
        )

        project.tasks.register("updateVersionInReadme", UpdateVersionInReadme::class.java)

        project.tasks.withType<Jar> {
            populateJarManifest(
                vendor = extension.vendor,
                project = project,
                builtDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            )
        }

        project.tasks.withType<Test> {
            xemanticTestLogging()
        }

    }

}

val Project.xemantic: XemanticGradleExtension
    get() = extensions.getByName("xemantic") as XemanticGradleExtension
