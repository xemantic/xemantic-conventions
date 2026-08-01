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

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import java.util.jar.JarFile

/**
 * The JUnit version used by fixture projects.
 *
 * Kept in sync with the version this build itself resolves, so that TestKit runs hit the
 * already populated Gradle cache instead of downloading a second JUnit. Drifting out of
 * sync only makes the suite slower, it does not make it fail.
 */
internal const val FIXTURE_JUNIT_VERSION = "5.10.1"

/**
 * A throwaway Gradle project, run through [GradleRunner] with the plugin under test
 * injected onto its classpath.
 *
 * The plugin is resolved via [GradleRunner.withPluginClasspath], which reads the metadata
 * produced by the `pluginUnderTestMetadata` task, so these tests exercise the *local*
 * sources rather than the published version this build applies to itself.
 */
internal class GradleProjectFixture(
    private val root: File
) {

    /**
     * Writes [content] to [path], creating parent directories as needed.
     */
    fun file(
        path: String,
        content: String
    ): File = File(root, path).apply {
        parentFile.mkdirs()
        writeText(content.trimIndent() + "\n")
    }

    fun read(path: String): String = File(root, path).readText()

    fun jar(path: String): JarFile = JarFile(File(root, path))

    /**
     * Writes the standard `settings.gradle.kts` naming the root project.
     */
    fun settings(rootProjectName: String) {
        file("settings.gradle.kts", """rootProject.name = "$rootProjectName"""")
    }

    fun build(vararg arguments: String): BuildResult = runner(arguments).build()

    fun buildAndFail(vararg arguments: String): BuildResult = runner(arguments).buildAndFail()

    private fun runner(arguments: Array<out String>) = GradleRunner.create()
        .withProjectDir(root)
        .withPluginClasspath()
        .withArguments(*arguments)

}
