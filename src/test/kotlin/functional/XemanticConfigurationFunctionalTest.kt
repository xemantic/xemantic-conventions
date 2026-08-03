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
import java.time.LocalDate

/**
 * Verifies that the values derived by `xemantic { }` observe what the build script configured,
 * rather than the defaults which are in place while the plugin is being applied.
 *
 * The probes read the properties at *configuration* time, into a local captured by the task
 * action, which is both what a real build script does - `footerMessage.set(xemantic.copyright)` -
 * and what keeps these fixtures compatible with the configuration cache.
 *
 * Regression test for issue #83, where the derived values were property initializers evaluated
 * in the extension constructor.
 */
class XemanticConfigurationFunctionalTest {

    @TempDir
    lateinit var projectDir: File

    private lateinit var project: GradleProjectFixture

    private val currentYear = LocalDate.now().year

    @BeforeEach
    fun setup() {
        project = GradleProjectFixture(projectDir)
        project.settings("repro")
    }

    private fun probeBuildScript(
        version: String,
        xemanticBlock: String
    ) = project.file("build.gradle.kts", """
        plugins {
            id("com.xemantic.gradle.xemantic-conventions")
        }

        version = "$version"

        xemantic {
        $xemanticBlock
        }

        val probes = mapOf(
            "url" to xemantic.url,
            "copyright" to xemantic.copyright,
            "releasePageUrl" to xemantic.releasePageUrl,
            "isReleaseBuild" to xemantic.isReleaseBuild.toString()
        )

        tasks.register("probe") {
            doLast {
                probes.forEach { (name, value) -> println("${'$'}name = ${'$'}value") }
            }
        }
    """)

    @Test
    fun `should derive url and releasePageUrl from the configured gitHubAccount and version`() {
        // given
        probeBuildScript(
            version = "1.0.0",
            xemanticBlock = """
                description = "Repro"
                inceptionYear = "2020"
                gitHubAccount = "acme"
                applyAllConventions()
            """
        )

        // when
        val result = project.build("probe")

        // then
        assert(result.output.contains("url = https://github.com/acme/repro")) { result.output }
        assert(
            result.output.contains(
                "releasePageUrl = https://github.com/acme/repro/releases/tag/v1.0.0"
            )
        ) { result.output }
    }

    @Test
    fun `should derive copyright from the configured inceptionYear and organization`() {
        // given
        probeBuildScript(
            version = "1.0.0",
            xemanticBlock = """
                description = "Repro"
                inceptionYear = "2020"
                organization = "Acme"
                applyAllConventions()
            """
        )

        // when
        val result = project.build("probe")

        // then
        assert(result.output.contains("copyright = © 2020-$currentYear Acme")) { result.output }
    }

    @Test
    fun `should collapse copyright to a single year when the project started this year`() {
        // given
        probeBuildScript(
            version = "1.0.0",
            xemanticBlock = """
                description = "Repro"
                inceptionYear = "$currentYear"
                applyAllConventions()
            """
        )

        // when
        val result = project.build("probe")

        // then
        assert(result.output.contains("copyright = © $currentYear Xemantic")) { result.output }
    }

    @Test
    fun `should keep an explicitly assigned copyright`() {
        // given
        probeBuildScript(
            version = "1.0.0",
            xemanticBlock = """
                description = "Repro"
                inceptionYear = "2020"
                copyright = "All rights reversed"
                applyAllConventions()
            """
        )

        // when
        val result = project.build("probe")

        // then
        assert(result.output.contains("copyright = All rights reversed")) { result.output }
    }

    @Test
    fun `should not report a release build for a version assigned by the build script`() {
        // given
        probeBuildScript(
            version = "1.0.0-SNAPSHOT",
            xemanticBlock = """
                description = "Repro"
                inceptionYear = "2020"
                applyAllConventions()
            """
        )

        // when
        val result = project.build("probe")

        // then
        assert(result.output.contains("isReleaseBuild = false")) { result.output }
    }

    @Test
    fun `should report a release build for a non snapshot version`() {
        // given
        probeBuildScript(
            version = "1.0.0",
            xemanticBlock = """
                description = "Repro"
                inceptionYear = "2020"
                applyAllConventions()
            """
        )

        // when
        val result = project.build("probe")

        // then
        assert(result.output.contains("isReleaseBuild = true")) { result.output }
    }

}
