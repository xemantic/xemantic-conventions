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

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class UpdateVersionInReadme @Inject constructor(
    log: ((String) -> Unit)?
) : DefaultTask() {

    private val log: ((String) -> Unit) = log ?: {
        logger.lifecycle(it)
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
        val regex = Regex("($projectGroup:$projectName:)\\d+(\\.\\d+)*(-\\w+)?")

        if (!regex.containsMatchIn(oldContent)) {
            throw GradleException(
                "No matching dependency reference found in README.md. " +
                        "Expected format: $projectGroup:$projectName:x.y.z"
            )
        }

        val updatedContent = oldContent.replace(regex, "$1$projectVersion")

        if (oldContent != updatedContent) {
            readmeFile.writeText(updatedContent)
            log("Successfully updated version in README.md to $projectVersion")
        } else {
            log("No update needed. Version in README.md is already $projectVersion")
        }
    }

}
