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

import kotlin.test.Test

class ExtractPlatformTest {

    @Test
    fun `should extract jvm from test task`() {
        val platform = extractPlatform("test")
        assert(platform == "jvm")
    }

    @Test
    fun `should extract jvm from jvmTest task`() {
        val platform = extractPlatform("jvmTest")
        assert(platform == "jvm")
    }

    @Test
    fun `should extract wasmJs from wasmJsTest task`() {
        val platform = extractPlatform("wasmJsTest")
        assert(platform == "wasmJs")
    }

    @Test
    fun `should extract js from jsTest task`() {
        val platform = extractPlatform("jsTest")
        assert(platform == "js")
    }

    @Test
    fun `should extract native from nativeTest task`() {
        val platform = extractPlatform("nativeTest")
        assert(platform == "native")
    }

    @Test
    fun `should extract linuxX64 from linuxX64Test task`() {
        val platform = extractPlatform("linuxX64Test")
        assert(platform == "linuxX64")
    }

    @Test
    fun `should extract macosArm64 from macosArm64Test task`() {
        val platform = extractPlatform("macosArm64Test")
        assert(platform == "macosArm64")
    }

    @Test
    fun `should return unknown for allTests task`() {
        // Note: allTests ends with "Tests" (plural), not "Test"
        val platform = extractPlatform("allTests")
        assert(platform == "unknown")
    }

    @Test
    fun `should return unknown for check task`() {
        val platform = extractPlatform("check")
        assert(platform == "unknown")
    }

    @Test
    fun `should return unknown for build task`() {
        val platform = extractPlatform("build")
        assert(platform == "unknown")
    }

}