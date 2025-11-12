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

import org.junit.jupiter.api.Test

class NextSnapshotVersionTest {

    @Test
    fun `should calculate next snapshot from patch release`() {
        val nextSnapshot = calculateNextSnapshotVersion("1.2.3")
        assert(nextSnapshot == "1.2.4-SNAPSHOT")
    }

    @Test
    fun `should calculate next snapshot from minor release`() {
        val nextSnapshot = calculateNextSnapshotVersion("1.2.0")
        assert(nextSnapshot == "1.2.1-SNAPSHOT")
    }

    @Test
    fun `should calculate next snapshot from major release`() {
        val nextSnapshot = calculateNextSnapshotVersion("2.0.0")
        assert(nextSnapshot == "2.0.1-SNAPSHOT")
    }

    @Test
    fun `should calculate next snapshot with incremented patch`() {
        val nextSnapshot = calculateNextSnapshotVersion("0.1.0")
        assert(nextSnapshot == "0.1.1-SNAPSHOT")
    }

    @Test
    fun `should calculate next snapshot from two-component version`() {
        val nextSnapshot = calculateNextSnapshotVersion("1.2")
        assert(nextSnapshot == "1.2.1-SNAPSHOT")
    }

    @Test
    fun `should calculate next snapshot from single-component version`() {
        val nextSnapshot = calculateNextSnapshotVersion("1")
        assert(nextSnapshot == "1.0.1-SNAPSHOT")
    }

    @Test
    fun `should handle patch overflow correctly`() {
        val nextSnapshot = calculateNextSnapshotVersion("1.2.999")
        assert(nextSnapshot == "1.2.1000-SNAPSHOT")
    }

    @Test
    fun `should handle zero version`() {
        val nextSnapshot = calculateNextSnapshotVersion("0.0.0")
        assert(nextSnapshot == "0.0.1-SNAPSHOT")
    }

    @Test
    fun `should handle version with pre-release suffix`() {
        // Per semver spec, nextPatch on pre-release version removes pre-release without incrementing
        // e.g., 1.2.3-alpha.1 -> 1.2.3 (the release version)
        val nextSnapshot = calculateNextSnapshotVersion("1.2.3-alpha.1")
        assert(nextSnapshot == "1.2.3-SNAPSHOT")
    }

    @Test
    fun `should handle version with build metadata`() {
        val nextSnapshot = calculateNextSnapshotVersion("1.2.3+build.123")
        assert(nextSnapshot == "1.2.4-SNAPSHOT")
    }
}
