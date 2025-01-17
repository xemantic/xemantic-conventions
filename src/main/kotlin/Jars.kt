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

import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar

/**
 * Each JAR file has also a manifest, and the manifest can contain additional
 * information related to the vendor and the build.
 */
fun Jar.populateJarManifest(
    vendor: String,
    project: Project,
    builtDate: String
) {
    manifest {
        attributes.let {
            it["Implementation-Title"] = archiveBaseName.get()
            it["Implementation-Version"] = archiveVersion.get()
            it["Implementation-Vendor"] = vendor
            it["Built-By"] = "Gradle"
            it["Built-Date"] = builtDate
        }
    }
    metaInf {
        from(project.rootProject.rootDir) {
            include("LICENSE")
        }
    }
}
