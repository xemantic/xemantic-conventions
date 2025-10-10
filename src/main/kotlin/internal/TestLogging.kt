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

import org.gradle.api.tasks.testing.AbstractTestTask
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.KotlinClosure2

/**
 * Rules for logging of tests.
 *
 * Note: the principle - we only want to log what's failing. The logs should be
 * convenient for digestion of not only a human, but also an AI agent performing the
 * build and the analysis of possible build failures.
 */
internal fun AbstractTestTask.configureTestReporting() {

    testLogging {
        events(
            TestLogEvent.SKIPPED
        )
        showStackTraces = false
        showExceptions = false
        showCauses = false
    }

    afterTest(KotlinClosure2({ descriptor: TestDescriptor, result: TestResult ->
        if (result.resultType == TestResult.ResultType.FAILURE) {
            val platform = extractPlatform(name)
            val testName = "${descriptor.className}.${descriptor.name}"

            logger.lifecycle("\n<test-failure name=\"$testName\" platform=\"$platform\">")
            logger.lifecycle("<message>")
            result.exceptions.forEach { exception ->
                logger.lifecycle(exception.message ?: exception.toString())
            }
            logger.lifecycle("</message>")

            result.exceptions.forEach { exception ->
                logger.lifecycle("<stacktrace>")
                exception.stackTrace.forEach { element ->
                    logger.lifecycle("  at $element")
                }
                logger.lifecycle("</stacktrace>")
            }

            logger.lifecycle("</test-failure>\n")
        }
    }))

}

/**
 * Extracts the platform name from a test task name.
 *
 * Examples:
 * - "jvmTest" -> "jvm"
 * - "wasmJsTest" -> "wasmJs"
 * - "test" -> "jvm" (default platform)
 * - "allTests" -> "all"
 * - "check" -> "unknown"
 */
private fun extractPlatform(taskName: String): String {
    return when {
        taskName == "test" -> "jvm"
        taskName.endsWith("Test") -> taskName.removeSuffix("Test")
        else -> "unknown"
    }
}
