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
 * Verifies the AI-friendly test log format produced for failing tests.
 *
 * The principle being guarded: only failures are reported, and a failure carries
 * everything needed to diagnose it - message, the output the test printed, and the
 * stack trace. Anything a passing test printed must stay out of the log.
 *
 * The fixture deliberately uses the `java` plugin rather than Kotlin, so that the suite
 * does not drag the Kotlin Gradle Plugin into every TestKit run.
 */
class TestReportingFunctionalTest {

    @TempDir
    lateinit var projectDir: File

    private lateinit var project: GradleProjectFixture

    @BeforeEach
    fun setup() {
        project = GradleProjectFixture(projectDir)
        project.settings("reporting-project")
        project.file("build.gradle.kts", """
            plugins {
                java
                id("com.xemantic.gradle.xemantic-conventions")
            }

            group = "com.example"
            version = "1.0.0"

            repositories {
                mavenCentral()
            }

            dependencies {
                testImplementation("org.junit.jupiter:junit-jupiter:$FIXTURE_JUNIT_VERSION")
                testRuntimeOnly("org.junit.platform:junit-platform-launcher")
            }

            tasks.withType<Test> {
                useJUnitPlatform()
            }

            xemantic {
                description = "A test project"
                inceptionYear = "2025"
                applyAllConventions()
            }
        """)
        project.file("src/test/java/SampleTest.java", """
            import org.junit.jupiter.api.Test;
            import static org.junit.jupiter.api.Assertions.fail;

            public class SampleTest {

                @Test
                public void passingTest() {
                    System.out.println("OUTPUT_FROM_PASSING_TEST");
                }

                @Test
                public void failingTest() {
                    System.out.println("OUTPUT_FROM_FAILING_TEST");
                    fail("deliberate failure");
                }

            }
        """)
    }

    @Test
    fun `should report a failing test as a test-failure element`() {
        // when
        val output = project.buildAndFail("test").output

        // then
        assert(output.contains("""<test-failure test="SampleTest.failingTest()" platform="jvm">"""))
        assert(output.contains("</test-failure>"))
    }

    @Test
    fun `should report the failure message and stack trace`() {
        // when
        val output = project.buildAndFail("test").output

        // then
        assert(output.contains("<message>"))
        assert(output.contains("deliberate failure"))
        assert(output.contains("<stacktrace>"))
        assert(output.contains("SampleTest.failingTest(SampleTest.java:"))
    }

    @Test
    fun `should report the output printed by the failing test`() {
        // when
        val output = project.buildAndFail("test").output

        // then
        assert(output.contains("<output>"))
        assert(output.contains("OUTPUT_FROM_FAILING_TEST"))
    }

    @Test
    fun `should not report anything for a passing test`() {
        // when
        val output = project.buildAndFail("test").output

        // then - the passing test is neither reported nor does its output leak
        assert(!output.contains("OUTPUT_FROM_PASSING_TEST"))
        assert(!output.contains("SampleTest.passingTest"))
    }

    @Test
    fun `should report a single failure when only one test fails`() {
        // when
        val output = project.buildAndFail("test").output

        // then
        assert(output.split("<test-failure ").size - 1 == 1)
    }

}
