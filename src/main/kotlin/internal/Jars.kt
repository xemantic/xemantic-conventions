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

import com.xemantic.gradle.conventions.xemantic
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar

/**
 * Populates JAR manifest with Xemantic-specific and build specific attributes.
 *
 * @param project the gradle project.
 */
internal fun Jar.populateJarManifest(
    project: Project,
) {
    manifest {
        attributes.let {
            it["Implementation-Title"] = archiveBaseName.get()
            it["Implementation-Version"] = archiveVersion.get()
            it["Implementation-Vendor"] = project.xemantic.organization
            it["Implementation-Vendor-Id"] = project.rootProject.name
            it["Created-By"] = "gradle"
        }
    }
    metaInf {
        from(project.rootProject.rootDir) {
            include("LICENSE")
        }
    }
}
