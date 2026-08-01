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

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * Verifies that the plugin can actually be applied by a real build, and that a
 * misconfigured `xemantic { }` block fails with the guidance message.
 */
class PluginApplicationFunctionalTest {

    @TempDir
    lateinit var projectDir: File

    private lateinit var project: GradleProjectFixture

    @BeforeEach
    fun setup() {
        project = GradleProjectFixture(projectDir)
        project.settings("test-project")
    }

    @Test
    fun `should register updateVersionsAfterRelease task in the xemantic group`() {
        // given
        project.file("build.gradle.kts", """
            plugins {
                id("com.xemantic.gradle.xemantic-conventions")
            }

            xemantic {
                description = "A test project"
                inceptionYear = "2025"
                applyAllConventions()
            }
        """)

        // when
        val result = project.build("tasks", "--group", "xemantic")

        // then
        assert(result.output.contains("updateVersionsAfterRelease"))
    }

    @Test
    fun `should fail with guidance when the xemantic block is missing`() {
        // given
        project.file("build.gradle.kts", """
            plugins {
                id("com.xemantic.gradle.xemantic-conventions")
            }
        """)

        // when
        val result = project.buildAndFail("tasks")

        // then
        assert(
            result.output.contains(
                "Remember to add xemantic { } section to your build.gradle.kts, " +
                        "and fill it with required parameters: description must be set"
            )
        )
    }

    @Test
    fun `should fail with guidance when inceptionYear is missing`() {
        // given
        project.file("build.gradle.kts", """
            plugins {
                id("com.xemantic.gradle.xemantic-conventions")
            }

            xemantic {
                description = "A test project"
            }
        """)

        // when
        val result = project.buildAndFail("tasks")

        // then
        assert(
            result.output.contains(
                "Remember to add xemantic { } section to your build.gradle.kts, " +
                        "and fill it with required parameters: inceptionYear must be set"
            )
        )
    }

}
