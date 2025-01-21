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

/**
 * Replaces the version of a Gradle dependency in a string.
 *
 * This function finds and replaces the version of a specified artifact in a Gradle dependency string.
 * It supports artifacts with variants and classifiers.
 *
 * Example:
 * ```
 * val originalString = """implementation "com.example:library:1.0.0""""
 * val updatedString = originalString.replaceGradleDependencyVersion("com.example:library", "2.0.0")
 * // updatedString will be: """implementation "com.example:library:2.0.0""""
 * ```
 *
 * @param artifact The name of the artifact to update.
 * @param newVersion The new version to set for the artifact.
 * @return A new string with the updated version for the specified artifact.
 */
internal fun String.replaceGradleDependencyVersion(
    artifact: String,
    newVersion: String
): String = replace(
    """"$artifact(-[^:]*)?:([^":]+)(:([^"]+))?"""".toRegex()
) { matchResult ->
    val (variant, _, _, classifier) = matchResult.destructured
    val classifierPart = if (classifier.isNotEmpty()) ":$classifier" else ""
    """"$artifact$variant:$newVersion$classifierPart""""
}
