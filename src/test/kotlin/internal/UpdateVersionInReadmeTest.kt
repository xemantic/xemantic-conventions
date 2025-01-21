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

import com.xemantic.kotlin.test.have
import com.xemantic.kotlin.test.should
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertFailsWith

class UpdateVersionInReadmeTest {

    @TempDir
    lateinit var testProjectDir: File

    private lateinit var project: Project
    private lateinit var task: UpdateVersionInReadme

    @BeforeEach
    fun setup() {
        project = ProjectBuilder.builder()
            .withProjectDir(testProjectDir)
            .withName("my-project")
            .build()
        project.group = "com.example"
        project.version = "1.0.1"
        task = project.tasks.register(
            "updateVersionInReadme",
            UpdateVersionInReadme::class.java
        ).get()
    }

    @Test
    fun `should update version in README when match found`() {
        // given
        val readme = File(testProjectDir, "README.md")
        readme.writeText("""
            # My Project
            
            ```kotlin
            dependencies {
                implementation("com.example:my-project:1.0.0")
            }
            ```
        """.trimIndent())

        // when
        task.action()

        // then
        val updatedContent = readme.readText()
        assert("""
            # My Project
            
            ```kotlin
            dependencies {
                implementation("com.example:my-project:1.0.1")
            }
            ```
        """.trimIndent() == updatedContent)
    }

    @Test
    fun `should fail when version in README is already correct`() {
        val readme = File(testProjectDir, "README.md")
        readme.writeText("""
            # My Project
            
            ```kotlin
            dependencies {
                implementation("com.example:my-project:1.0.1")
            }
            ```
        """.trimIndent())

        assertFailsWith<GradleException> {
            task.action()
        } should {
            have(
                message == "Dependency is either already the most recent version, " +
                        "or no matching dependency reference found in README.md. " +
                        "Expected format: com.example:my-project[-platform]:x.y.z[:classifier]"
            )
        }
    }

    @Test
    fun `should fail when no match is found in README`() {
        val readme = File(testProjectDir, "README.md")
        readme.writeText("""
            # My Project
            
            This is a sample project.
        """.trimIndent())

        assertFailsWith<GradleException> {
            task.action()
        } should {
            have(
                message == "Dependency is either already the most recent version, " +
                        "or no matching dependency reference found in README.md. " +
                        "Expected format: com.example:my-project[-platform]:x.y.z[:classifier]"
            )
        }
    }

    @Test
    fun `should fail when README file not found`() {
        assertFailsWith<GradleException> {
            task.action()
        } should {
            have(message == "README.md file not found in the project root directory.")
        }
    }

}