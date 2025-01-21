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

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Defines `xemantic:updateVersionInReadme` gradle task.
 */
internal abstract class UpdateVersionInReadme : DefaultTask() {

    init {
       group = "xemantic"
       description = "Updates artifact version in README.md file"
    }

    @TaskAction
    fun action() {
        val projectGroup = project.group.toString()
        val projectName = project.name
        val projectVersion = project.version.toString()

        val readmeFile = File(project.rootDir, "README.md")
        if (!readmeFile.exists()) {
            throw GradleException(
                "README.md file not found in the project root directory."
            )
        }

        val oldContent = readmeFile.readText()
        val newContent = oldContent.replaceGradleDependencyVersion(
            artifact = "$projectGroup:$projectName",
            newVersion = projectVersion
        )

        if (newContent == oldContent) {
            throw GradleException(
                "Dependency is either already the most recent version, " +
                        "or no matching dependency reference found in README.md. " +
                        "Expected format: $projectGroup:$projectName[-platform]:x.y.z[:classifier]"
            )
        } else {
            readmeFile.writeText(newContent)
            logger.lifecycle(
                "Successfully updated version in README.md to $projectVersion"
            )
        }
    }

}