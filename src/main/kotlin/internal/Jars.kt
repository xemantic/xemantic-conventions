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

import com.xemantic.gradle.conventions.XemanticConfiguration
import org.gradle.jvm.tasks.Jar

/**
 * Populates JAR manifest with Xemantic-specific and build specific attributes.
 *
 * The attribute values are supplied as providers rather than resolved eagerly. When the root
 * project applies these conventions to its subprojects, this configuration runs while the
 * subproject's `Jar` task is still being created - before the `base` plugin has had a chance to
 * set the `archiveBaseName` convention - so reading the values here would fail with
 * "Cannot query the value of task ':sub:jar' property 'archiveBaseName'".
 *
 * The configuration is taken from [config] rather than looked up on the task's own project,
 * because in a multimodule build the `xemantic { }` extension only exists in the project which
 * applies the plugin - typically the root - while the `Jar` tasks being configured belong to
 * the subprojects.
 *
 * @param config the configuration of the project applying the conventions.
 */
internal fun Jar.populateJarManifest(
    config: XemanticConfiguration,
) {
    manifest {
        attributes.let {
            it["Implementation-Title"] = archiveBaseName
            it["Implementation-Version"] = archiveVersion
            it["Implementation-Vendor"] = project.provider { config.organization }
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
