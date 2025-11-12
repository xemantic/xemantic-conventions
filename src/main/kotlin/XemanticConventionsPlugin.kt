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

import com.xemantic.gradle.conventions.internal.*
import org.gradle.api.Plugin
import org.gradle.api.Project

public class XemanticConventionsPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        project.tasks.register(
            "updateVersionInReadme",
            UpdateVersionInReadme::class.java
        )

        val xemantic = project.extensions.create(
            "xemantic",
            XemanticConfiguration::class.java
        )

        project.afterEvaluate {
            xemantic.validate()
        }

    }

}

public val Project.xemantic: XemanticConfiguration
    get() = extensions.getByName("xemantic") as XemanticConfiguration
