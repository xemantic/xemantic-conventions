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

import kotlin.test.Test

class ReplaceGradleDependencyVersionTest {

    @Test
    fun `should replace Gradle dependency version for a minor release from a single component major release`() {
        val input = """
            dependencies {
                implementation("com.example:my-project:1")
            }
        """.trimIndent()
        val expected = """
            dependencies {
                implementation("com.example:my-project:1.1")
             }
        """.trimIndent()
        assert(input.replaceGradleDependencyVersion("com.example:my-project", "1.1") == expected)
    }

    @Test
    fun `should replace Gradle dependency version for a patch release from a single component major release`() {
        val input = """
            dependencies {
                implementation("com.example:my-project:1")
            }
        """.trimIndent()
        val expected = """
            dependencies {
                implementation("com.example:my-project:1.0.1")
             }
        """.trimIndent()
        assert(input.replaceGradleDependencyVersion("com.example:my-project", "1.0.1") == expected)
    }

    @Test
    fun `should replace Gradle dependency version for a minor release from 2-component release`() {
        val input = """
            dependencies {
                implementation("com.example:my-project:1.0")
            }
        """.trimIndent()
        val expected = """
            dependencies {
                implementation("com.example:my-project:1.1")
             }
        """.trimIndent()
        assert(input.replaceGradleDependencyVersion("com.example:my-project", "1.1") == expected)
    }

    @Test
    fun `should replace Gradle dependency version for a patch release from 2-component release`() {
        val input = """
            dependencies {
                implementation("com.example:my-project:1.0")
            }
        """.trimIndent()
        val expected = """
            dependencies {
                implementation("com.example:my-project:1.0.1")
             }
        """.trimIndent()
        assert(input.replaceGradleDependencyVersion("com.example:my-project", "1.0.1") == expected)
    }

    @Test
    fun `should replace Gradle dependency version for a full semantic version`() {
        val input = """
            dependencies {
                implementation("com.example:my-project:1.0.0")
            }
        """.trimIndent()
        val expected = """
            dependencies {
                implementation("com.example:my-project:2.0.1")
            }
        """.trimIndent()
        assert(input.replaceGradleDependencyVersion("com.example:my-project", "2.0.1") == expected)
    }

    @Test
    fun `should replace Gradle dependency version for a full semantic version to 2-component release`() {
        val input = """
            dependencies {
                implementation("com.example:my-project:1.2.3")
            }
        """.trimIndent()
        val expected = """
            dependencies {
                implementation("com.example:my-project:2.0")
            }
        """.trimIndent()
        assert(input.replaceGradleDependencyVersion("com.example:my-project", "2.0") == expected)
    }

    @Test
    fun `should replace multiple occurrences of the same dependency`() {
        val input = """
            dependencies {
                implementation("com.example:my-project:1.0.0")
                testImplementation("com.example:my-project:1.0.0")
            }
        """.trimIndent()
        val expected = """
            dependencies {
                implementation("com.example:my-project:2.0.0")
                testImplementation("com.example:my-project:2.0.0")
            }
        """.trimIndent()
        assert(input.replaceGradleDependencyVersion("com.example:my-project", "2.0.0") == expected)
    }

    @Test
    fun `should not replace version for different dependency`() {
        val input = """
            dependencies {
                implementation("com.example:my-project:1.0.0")
                implementation("com.another:other-project:2.0.0")
            }
        """.trimIndent()
        val expected = """
            dependencies {
                implementation("com.example:my-project:1.1.0")
                implementation("com.another:other-project:2.0.0")
            }
        """.trimIndent()
        assert(input.replaceGradleDependencyVersion("com.example:my-project", "1.1.0") == expected)
    }

    @Test
    fun `should handle dependency with classifier`() {
        val input = """
            dependencies {
                implementation("com.example:my-project:1.0.0:jdk8")
            }
        """.trimIndent()
        val expected = """
            dependencies {
                implementation("com.example:my-project:2.0.0:jdk8")
            }
        """.trimIndent()
        assert(input.replaceGradleDependencyVersion("com.example:my-project", "2.0.0") == expected)
    }

    @Test
    fun `should handle dependency with exclusion`() {
        val input = """
            dependencies {
                implementation("com.example:my-project:1.0.0") {
                    exclude(group = "org.unwanted", module = "unwanted-dependency")
                }
            }
        """.trimIndent()
        val expected = """
            dependencies {
                implementation("com.example:my-project:1.1.0") {
                    exclude(group = "org.unwanted", module = "unwanted-dependency")
                }
            }
        """.trimIndent()
        assert(input.replaceGradleDependencyVersion("com.example:my-project", "1.1.0") == expected)
    }

    @Test
    fun `should handle dependency with multiplatform variant`() {
        val input = """
            dependencies {
                implementation("com.example:my-project-jvm:1.0.0")
            }
        """.trimIndent()
        val expected = """
            dependencies {
                implementation("com.example:my-project-jvm:2.0.0")
            }
        """.trimIndent()
        assert(input.replaceGradleDependencyVersion("com.example:my-project", "2.0.0") == expected)
    }

}
