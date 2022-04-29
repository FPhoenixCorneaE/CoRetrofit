plugins {

    /**
     * You should use `apply false` in the top-level build.gradle file
     * to add a Gradle plugin as a build dependency, but not apply it to the
     * current (root) project. You should not use `apply false` in sub-projects.
     * For more information, see
     * Applying external plugins with same version to subprojects.
     */

    id(Deps.PluginIds.application) version Deps.Versions.agpVersion apply false
    id(Deps.PluginIds.library) version Deps.Versions.agpVersion apply false
    id(Deps.PluginIds.kotlin) version Deps.Versions.kotlinVersion apply false
}

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}