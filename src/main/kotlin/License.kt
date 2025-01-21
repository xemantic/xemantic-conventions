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

/**
 * The list of open source licenses which can be used by xemantic projects.
 */
@Suppress("unused")
public enum class License(
    public val id: String,
    public val url: String,
    public val spxdx: String
) {
    APACHE(
        id = "The Apache Software License, Version 2.0",
        url = "http://www.apache.org/licenses/LICENSE-2.0.txt",
        spxdx = "Apache-2.0"
    ),
    GPL(
        id = "GNU General Public License, Version 3.0",
        url = "https://www.gnu.org/licenses/gpl-3.0.txt",
        spxdx = "GPL-3.0"
    ),
    LGPL(
        id = "GNU Lesser General Public License, Version 3.0",
        url = "https://www.gnu.org/licenses/lgpl-3.0.txt",
        spxdx = "LGPL-3.0"
    )
}
