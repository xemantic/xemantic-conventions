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
 *
 * This task performs two updates when releasing:
 * 1. Updates the artifact version in README.md to the current release version
 * 2. Updates the version in gradle.properties to the next snapshot version
 */
internal abstract class UpdateVersionInReadme : DefaultTask() {

    init {
        group = "xemantic"
        description = "Updates artifact version in README.md and gradle.properties with next snapshot version"
    }

    @TaskAction
    fun action() {
        val projectGroup = project.group.toString()
        val projectName = project.name
        val projectVersion = project.version.toString()

        // Update README.md with current release version
        updateReadmeFile(projectGroup, projectName, projectVersion)

        // Update gradle.properties with next snapshot version
        updateGradleProperties(projectVersion)
    }

    private fun updateReadmeFile(
        projectGroup: String,
        projectName: String,
        projectVersion: String
    ) {
        val readmeFile = File(project.rootDir, "README.md")
        if (!readmeFile.exists()) {
            throw GradleException(
                "README.md file not found in the project root directory."
            )
        }

        val oldContent = readmeFile.readText()

        // For xemantic-conventions project, also update TOML version reference
        val newContent = if (projectName == "xemantic-conventions") {
            oldContent
                .replaceGradleDependencyVersion(
                    artifact = "$projectGroup:$projectName",
                    newVersion = projectVersion
                )
                .replaceTomlVersion(
                    versionRefName = "xemanticConventionsPlugin",
                    newVersion = projectVersion
                )
        } else {
            oldContent.replaceGradleDependencyVersion(
                artifact = "$projectGroup:$projectName",
                newVersion = projectVersion
            )
        }

        if (newContent == oldContent) {
            throw GradleException(
                "Dependency is either already the most recent version, " +
                        "or no matching dependency reference found in README.md. " +
                        "Expected format: $projectGroup:$projectName[-platform]:x.y.z[:classifier]" +
                        if (projectName == "xemantic-conventions") {
                            " or xemanticConventionsPlugin = \"x.y.z\""
                        } else {
                            ""
                        }
            )
        } else {
            readmeFile.writeText(newContent)
            logger.lifecycle(
                "Successfully updated version in README.md to $projectVersion"
            )
        }
    }

    private fun updateGradleProperties(projectVersion: String) {
        val gradlePropertiesFile = File(project.rootDir, "gradle.properties")
        if (!gradlePropertiesFile.exists()) {
            logger.lifecycle(
                "gradle.properties file not found, skipping version update"
            )
            return
        }

        // Calculate next snapshot version
        val nextSnapshot = try {
            calculateNextSnapshotVersion(projectVersion)
        } catch (e: Exception) {
            logger.warn(
                "Failed to calculate next snapshot version from $projectVersion: ${e.message}. " +
                        "gradle.properties will not be updated."
            )
            return
        }

        val oldContent = gradlePropertiesFile.readText()
        val versionRegex = Regex("^version\\s*=\\s*(.+)$", RegexOption.MULTILINE)

        val newContent = versionRegex.replace(oldContent) {
            "version=$nextSnapshot"
        }

        if (newContent != oldContent) {
            gradlePropertiesFile.writeText(newContent)
            logger.lifecycle(
                "Successfully updated version in gradle.properties to $nextSnapshot"
            )
        } else {
            logger.warn(
                "No version property found in gradle.properties"
            )
        }
    }

}