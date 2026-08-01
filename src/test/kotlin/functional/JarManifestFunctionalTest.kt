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

import com.xemantic.kotlin.test.have
import com.xemantic.kotlin.test.should
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * Verifies that the produced JAR carries the Xemantic manifest attributes and the license.
 */
class JarManifestFunctionalTest {

    @TempDir
    lateinit var projectDir: File

    private lateinit var project: GradleProjectFixture

    @BeforeEach
    fun setup() {
        project = GradleProjectFixture(projectDir)
        project.settings("manifest-project")
        project.file("build.gradle.kts", """
            plugins {
                java
                id("com.xemantic.gradle.xemantic-conventions")
            }

            group = "com.example"
            version = "2.3.4"

            xemantic {
                description = "A test project"
                inceptionYear = "2025"
                applyAllConventions()
            }

            // mimics the javadoc jar registered by the maven-publish plugin, which is typed as
            // org.gradle.jvm.tasks.Jar - the supertype of org.gradle.api.tasks.bundling.Jar,
            // so matching on the latter would silently skip it
            tasks.register<org.gradle.jvm.tasks.Jar>("docsJar") {
                archiveClassifier.set("javadoc")
            }
        """)
        project.file("LICENSE", "Apache License, Version 2.0")
    }

    @Test
    fun `should populate the manifest with Xemantic attributes`() {
        // when
        project.build("jar")

        // then
        project.jar("build/libs/manifest-project-2.3.4.jar").use { jar ->
            jar.manifest.mainAttributes should {
                have(getValue("Implementation-Title") == "manifest-project")
                have(getValue("Implementation-Version") == "2.3.4")
                have(getValue("Implementation-Vendor") == "Xemantic")
                have(getValue("Implementation-Vendor-Id") == "manifest-project")
                have(getValue("Created-By") == "gradle")
            }
        }
    }

    @Test
    fun `should include the LICENSE in META-INF`() {
        // when
        project.build("jar")

        // then
        project.jar("build/libs/manifest-project-2.3.4.jar").use { jar ->
            assert(jar.getEntry("META-INF/LICENSE") != null)
        }
    }

    @Test
    fun `should populate manifests of subprojects when applied to the root project`() {
        // given
        project.file("settings.gradle.kts", """
            rootProject.name = "manifest-project"
            include(":lib")
        """)
        project.file("build.gradle.kts", """
            plugins {
                id("com.xemantic.gradle.xemantic-conventions")
            }

            group = "com.example"
            version = "2.3.4"

            xemantic {
                description = "A test project"
                inceptionYear = "2025"
                applyAllConventions()
            }
        """)
        project.file("lib/build.gradle.kts", """
            plugins {
                java
            }

            group = "com.example"
            version = "2.3.4"
        """)

        // when
        project.build(":lib:jar")

        // then
        project.jar("lib/build/libs/lib-2.3.4.jar").use { jar ->
            jar.manifest.mainAttributes should {
                have(getValue("Implementation-Title") == "lib")
                have(getValue("Implementation-Version") == "2.3.4")
                have(getValue("Implementation-Vendor") == "Xemantic")
                have(getValue("Implementation-Vendor-Id") == "manifest-project")
            }
            assert(jar.getEntry("META-INF/LICENSE") != null)
        }
    }

    @Test
    fun `should also populate jars typed as the jvm Jar supertype`() {
        // when
        project.build("docsJar")

        // then
        project.jar("build/libs/manifest-project-2.3.4-javadoc.jar").use { jar ->
            jar.manifest.mainAttributes should {
                have(getValue("Implementation-Title") == "manifest-project")
                have(getValue("Implementation-Version") == "2.3.4")
                have(getValue("Implementation-Vendor") == "Xemantic")
                have(getValue("Implementation-Vendor-Id") == "manifest-project")
                have(getValue("Created-By") == "gradle")
            }
            assert(jar.getEntry("META-INF/LICENSE") != null)
        }
    }

}
