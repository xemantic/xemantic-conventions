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
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jreleaser.gradle.plugin.JReleaserExtension

internal fun Project.configureJReleaserConventions(
    config: XemanticConfiguration
) {

    // we are not releasing with jreleaser, just announcing
    // still we need default values here to pass validations
    extensions.configure<JReleaserExtension> {

        release {
            github {
                skipRelease.set(true)
                skipTag.set(true)
                token.set("empty")
                changelog {
                    enabled.set(false)
                }
            }
        }

        project {
            description.set(config.description)
            copyright.set(config.copyright)
            license.set("xemantic")
            links {
                homepage.set("https://example.com")
                documentation.set("https://example.com")
            }
            authors.set(listOf("xemantic"))
        }

    }

}
