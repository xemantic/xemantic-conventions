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

private val DEPENDENCY_REGEX = """"([^:]+:[^:]+(?:-[^:]+)?):([^:"]+)(?::([^"]+))?"""".toRegex()

fun String.replaceGradleDependencyVersion(
    artifact: String,
    newVersion: String
): String {
    return replace(DEPENDENCY_REGEX) { matchResult ->
        val (fullMatch, matchedArtifact, _, classifier) = matchResult.groupValues
        if (matchedArtifact.startsWith(artifact)) {
            if (classifier.isEmpty()) {
                """"$matchedArtifact:$newVersion""""
            } else {
                """"$matchedArtifact:$newVersion:$classifier""""
            }
        } else {
            fullMatch
        }
    }
}
