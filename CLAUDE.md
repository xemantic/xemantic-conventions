# CLAUDE.md

This file captures only what cannot be inferred from the codebase itself.

## Rules for editing this file

Both developers and AI agents are expected to add entries as they encounter surprises.

- **Add an entry** when you encounter something unexpected: a build quirk, a non-obvious constraint, a dependency gotcha, or any behavior that would surprise the next agent or developer.
- **Add an entry** when a developer flags an anti-pattern produced by AI — describe the anti-pattern and the preferred alternative.
- **Do not** add codebase overviews, directory listings, or anything discoverable by reading the source.
- Keep entries concise: one line per lesson, grouped under a heading if a theme emerges.

## Conventions

### Markdown authoring

Markdown files use [semantic line breaks](https://sembr.org/):
break a line after a sentence,
and optionally at clause boundaries within a long sentence,
so that diffs stay meaningful and reviewable.

There is no column width limit —
never reflow or hard-wrap a paragraph to fit some character count.
Modern editors soft-wrap Markdown visually,
see the [README](README.md#markdown-soft-wrapping-in-the-ide) for how to enable it.

## Known gotchas

- This plugin applies itself ("eating own dog food"),
  but it resolves the *published* version pinned as `xemanticConventionsPlugin` in `gradle/libs.versions.toml` — not the local sources.
  A convention added here has no effect on this project's own build until it is released and that version is bumped,
  and a broken release can break this build.
- Publishing and JReleaser tasks are run with `--no-configuration-cache` in CI;
  run them the same way locally, otherwise they fail at configuration time.
- Version numbers in `README.md` and `gradle.properties` are rewritten by the `updateVersionsAfterRelease` task and committed by CI after each release — do not hand-edit them.
- The version catalog update plugin drops *trailing* comments in `libs.versions.toml` — `foo = "1.0" # why this version` is silently lost on `versionCatalogFormat`/`versionCatalogUpdate`,
  while a comment on its own line above the entry survives.
  Always put catalog comments on their own line.
- The `kotlin` version follows the Gradle version rather than the newest Kotlin release, which is why it is `pin`ned in `versionCatalogUpdate` and bumped by hand together with the wrapper:
  it has to match the version embedded by `kotlin-dsl`, otherwise that plugin warns that it "relies on features of Kotlin `x.y.z` that might work differently",
  and it has to be recent enough to read the Kotlin metadata of the Gradle plugin APIs the conventions compile against, otherwise compilation fails with "was compiled with an incompatible version of Kotlin".
  A Kotlin compiler reads metadata only one minor version ahead of itself, so those two bounds can genuinely conflict — they did on Gradle 9.2, which embedded Kotlin 2.2.
- Upgrading the Gradle wrapper can fail `validatePlugins` with rules that did not exist before — 9.6 started requiring every task class to declare `@CacheableTask`, `@DisableCachingByDefault` or `@UntrackedTask`.
  Run `./gradlew validatePlugins` after a wrapper bump.
- These conventions are applied once, in the project owning the `xemantic { }` block, and reach the subprojects through `allprojects { }` — which makes two things easy to get wrong:
  a `configureEach` callback registered from the root runs *before* the subproject's own plugins set their conventions,
  so reading a task property there (`archiveBaseName.get()`) fails with "Cannot query the value ... because it has no value available" — pass providers instead of resolved values;
  and inside `configureEach` the implicit `project` is the *task's* project, so `project.xemantic` throws "Extension with name 'xemantic' does not exist" for every subproject — take the configuration from the captured `XemanticConfiguration` instead.
- The `publishToMavenCentral` task is registered by `com.vanniktech.maven.publish` only when Maven Central publishing is enabled through the `mavenCentralPublishing` or `SONATYPE_HOST` Gradle property,
  so it does not exist in a plain local build — CI passes it, which is why release-only task wiring cannot be verified by simply running the task locally.
- Do not switch `versionCatalogUpdate` to the built-in `VersionSelectors.STABLE`:
  its `isStable` regex is `^[0-9,.v-]+(-r)?$`, so it treats every qualifier as unstable, including classifiers like `-jre` or `-android`, and silently *downgrades* such dependencies —
  measured, it takes `guava = "30.0-jre"` down to `"23.0"`.
  The default `PREFER_STABLE` is the safe choice; for keyword-based rules use `versionSelector { }`, which needs an explicit `import nl.littlerobots.vcu.plugin.versionSelector`
  because `ModuleVersionSelector` is a plain interface rather than a `fun interface`, so the lambda is not SAM-converted.
- Gradle has two `Jar` task types — `org.gradle.api.tasks.bundling.Jar` *extends* `org.gradle.jvm.tasks.Jar`.
  Always match on the `jvm.tasks` supertype in `withType<Jar>()`, otherwise the match silently skips jars registered by other plugins:
  the javadoc jar of `com.vanniktech.maven.publish` extends the supertype, so matching on `bundling.Jar` published it without the manifest attributes and without `META-INF/LICENSE`.
- Never invoke `apiDump` together with `build` or `check` in a single Gradle run:
  `apiCheck` reads the file `apiDump` writes, and Gradle fails the run on the undeclared task dependency.
  Run `./gradlew apiDump` first, then `./gradlew build`.

## Anti-patterns to avoid

- Do not add content to this file that is already discoverable by reading the source or build scripts — that inflates context without adding signal, reducing AI agent task success rates (see [arxiv 2602.11988](https://arxiv.org/abs/2602.11988)).
