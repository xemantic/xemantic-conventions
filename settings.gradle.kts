val groupId = "com.xemantic.kotlin"
val name = "xemantic-gradle-plugin"

rootProject.name = name
gradle.beforeProject {
    group = groupId
}
