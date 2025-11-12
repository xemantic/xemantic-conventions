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

import io.github.z4kn4fein.semver.nextPatch
import io.github.z4kn4fein.semver.toVersion

/**
 * Calculates the next snapshot version from a release version.
 *
 * This function takes a semantic version string (e.g., "1.2.3") and calculates
 * the next patch version with "-SNAPSHOT" suffix (e.g., "1.2.4-SNAPSHOT").
 *
 * The function uses loose parsing mode to accept versions with fewer than 3 components:
 * - "1.2" is parsed as "1.2.0"
 * - "1" is parsed as "1.0.0"
 *
 * Pre-release identifiers and build metadata are ignored when calculating the next version.
 *
 * @param releaseVersion The release version string (e.g., "1.2.3", "0.1.0", "1.2")
 * @return The next snapshot version string (e.g., "1.2.4-SNAPSHOT")
 * @throws IllegalArgumentException if the version string is invalid
 */
internal fun calculateNextSnapshotVersion(releaseVersion: String): String {
    val version = releaseVersion.toVersion(strict = false)
    val nextVersion = version.nextPatch()
    return "$nextVersion-SNAPSHOT"
}
