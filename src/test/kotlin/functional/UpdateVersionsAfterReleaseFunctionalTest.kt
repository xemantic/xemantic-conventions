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

package com.xemantic.gradle.conventions.functional

import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * Drives `updateVersionsAfterRelease` through a real Gradle invocation.
 *
 * The unit tests call the task action directly; this suite additionally covers the wiring -
 * that the task is reachable by name from a real build, and that a second invocation re-runs
 * rather than being skipped as `UP-TO-DATE`.
 *
 * Note that the `@UntrackedTask` annotation on the task is *not* what these tests pin down -
 * the task declares no outputs, so Gradle re-runs it either way. That annotation is enforced
 * by `validatePlugins`, which fails the build when it is missing.
 */
class UpdateVersionsAfterReleaseFunctionalTest {

    @TempDir
    lateinit var projectDir: File

    private lateinit var project: GradleProjectFixture

    @BeforeEach
    fun setup() {
        project = GradleProjectFixture(projectDir)
        project.settings("my-project")
        project.file("build.gradle.kts", """
            plugins {
                id("com.xemantic.gradle.xemantic-conventions")
            }

            group = "com.example"
            version = "1.0.1"

            xemantic {
                description = "A test project"
                inceptionYear = "2025"
                applyAllConventions()
            }
        """)
        project.file("README.md", """
            # My Project

            ```kotlin
            dependencies {
                implementation("com.example:my-project:1.0.0")
            }
            ```
        """)
        project.file("gradle.properties", "version=1.0.0-SNAPSHOT")
    }

    @Test
    fun `should update the release version in README`() {
        // when
        project.build("updateVersionsAfterRelease")

        // then
        assert(project.read("README.md").contains("""implementation("com.example:my-project:1.0.1")"""))
    }

    @Test
    fun `should update gradle properties to the next snapshot`() {
        // when
        project.build("updateVersionsAfterRelease")

        // then
        assert(project.read("gradle.properties").contains("version=1.0.2-SNAPSHOT"))
    }

    @Test
    fun `should never be considered up-to-date`() {
        // given
        project.build("updateVersionsAfterRelease")

        // when - the README now already carries 1.0.1, so a task that really re-runs must
        // fail on the second invocation rather than report UP-TO-DATE
        val result = project.buildAndFail("updateVersionsAfterRelease")

        // then
        assert(result.task(":updateVersionsAfterRelease")?.outcome == TaskOutcome.FAILED)
        assert(
            result.output.contains(
                "Dependency is either already the most recent version, " +
                        "or no matching dependency reference found in README.md"
            )
        )
    }

}
