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
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension

@Suppress("unused") // used in build.gradle.kts
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

            allprojects {

                tasks.withType<Jar> {
                    populateJarManifest(project)
                }

                tasks.withType<Test> {
                    xemanticTestLogging()
                }

                configure<PublishingExtension> {

                    val publishing = this

                    repositories {
                        if (xemantic.isReleaseBuild) {
                            maven {
                                url = xemantic.stagingDeployDir.toURI()
                            }
                        } else {
                            if ("githubActor" in project.properties) {
                                maven {
                                    name = "GitHubPackages"
                                    setUrl("https://maven.pkg.github.com/${xemantic.gitHubAccount}/${rootProject.name}")
                                    credentials {
                                        username = project.properties["githubActor"]!!.toString()
                                        password = project.properties["githubToken"]!!.toString()
                                    }
                                }
                            }
                        }
                    }

                    if ("signingKey" in project.properties) {
                        configure<SigningExtension> {
                            useInMemoryPgpKeys(
                                project.properties["signingKey"]!!.toString(),
                                project.properties["signingPassword"]!!.toString()
                            )
                            sign(publishing.publications)
                        }
                    }

                }

                applyWorkarounds(xemantic)
            }

        }

    }

}

public val Project.xemantic: XemanticConfiguration
    get() = extensions.getByName("xemantic") as XemanticConfiguration

