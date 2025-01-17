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

import javax.inject.Inject

abstract class XemanticGradleExtension @Inject constructor(
    private val currentYear: Int
) {

    var description: String? = null

    var inceptionYear: Int? = null

    var license: License? = null

    var vendor: String = "Xemantic"

    var vendorUrl: String = "https://xemantic.com"

    val copyright: String get() =
    "Copyright © ${if (inceptionYear != currentYear) inceptionYear.toString() else ""}-$currentYear $vendor"

    var gitHubAccount: String = "xemantic"

//    vararg develpers: List<Developer>

    private val _developers = mutableListOf<Developer>()
    val developers: List<Developer> = _developers

    fun developer(
        id: String,
        name: String,
        email: String
    ) {
        _developers += Developer(id, name, email)
    }

    fun validate() {
        requireNotNull(description) { "description must be set" }
        requireNotNull(inceptionYear) { "inceptionYear must be set" }
        requireNotNull(license) { "license must be set" }
        require(_developers.isNotEmpty()) { "at least one developer must be added" }
    }

}

data class Developer(
    val id: String,
    val name: String,
    val email: String
)
