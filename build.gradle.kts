@file:Suppress("SpellCheckingInspection")

import io.github.wasabithumb.gmp.MesonPluginExtension
import io.github.wasabithumb.gmp.option.MesonBuildType
import io.github.wasabithumb.gmp.option.MesonOptimizationLevel
import io.github.wasabithumb.gmp.task.MesonSetupTask


plugins {
    id("application")
    id("io.freefair.lombok") version "9.0.0"
    id("io.github.wasabithumb.gradle-meson-plugin") version "0.1.0"
    id("com.gradleup.shadow") version "9.2.2"
    kotlin("jvm")
}

group = "io.ibnuja"
version = "1.0-SNAPSHOT"

val slf4jVersion = "2.0.17"
val log4jVersion = "2.25.2"
val junitVersion = "5.10.0"
val jacksonBomVersion = "2.20.0"
val javaGiVersion = "0.13.0"
val ktorVersion = "3.3.2"
val subsonicApiVersion = "1.1.1"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/eap")
}

val localPrefix = "${System.getProperty("user.home")}/.local"

meson {
    configuration("linux-amd64") {
        buildType = MesonBuildType.RELEASE
        optimization = MesonOptimizationLevel.O3
        options["prefix"] = localPrefix
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.named<MesonSetupTask>("mesonSetup") {
    dependsOn("shadowJar")
}

val commonJvmArgs = mutableListOf(
    "--enable-native-access=ALL-UNNAMED",
)
val mesonExt = extensions.getByType<MesonPluginExtension>()

val mesonInstall = tasks.register("mesonInstall") {
    group = "build"
    description = "Installs all enabled Meson configurations"
    dependsOn("mesonSetup", "shadowJar")
}

mesonExt.configurations.forEach { (configName, config) ->
    if (config.enabled) {
        val taskName = "mesonInstall${configName.replaceFirstChar { it.uppercase() }}"

        tasks.register<Exec>(taskName) {
            group = "build"
            description = "Installs the $configName configuration"
            dependsOn("mesonCompile")

            val buildDir = layout.buildDirectory.dir("meson/$configName").get().asFile
            workingDir = buildDir

            commandLine("meson", "install")

            onlyIf { buildDir.exists() }
        }

        mesonInstall.configure {
            dependsOn(taskName)
        }
    }
}

tasks.named("shadowJar") {
    dependsOn("compileResources")
}

tasks.named<JavaExec>("run") {
    dependsOn("compileResources")
    args(
        "Hypersonic",
    )
}

tasks.register<Exec>("compileBlueprints") {
    group = "build"
    description = "Compile Blueprint files into GtkBuilder XML."
    workingDir = file("src/main/resources")

    val inputFiles = listOf(
        "window.blp",
        //components
        "components/settings/settings.blp",
        "components/playback/playback_info.blp",
        "components/playback/playback_controls.blp",
        "components/playback/playback_widget.blp",
        "components/selection/selection_toolbar.blp",
        "components/sidebar/sidebar_row.blp",
        //pages
        "pages/home.blp",
    )

    commandLine(
        "blueprint-compiler",
        "batch-compile",
        "blueprint-compiler",
        ".",
        *inputFiles.toTypedArray()
    )
}

tasks.register<Exec>("compileResources") {
    group = "build"
    description = "Compile GResource XML into a binary resource file."
    workingDir = file("src/main/resources")
    dependsOn("compileBlueprints")
    commandLine = listOf("glib-compile-resources", "hypersonicapp.gresource.xml")
}

dependencies {
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:${slf4jVersion}")
    implementation(platform("org.apache.logging.log4j:log4j-bom:${log4jVersion}"))
    implementation(platform("com.fasterxml.jackson:jackson-bom:${jacksonBomVersion}"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("org.java-gi:gtk:${javaGiVersion}")
    implementation("org.java-gi:adw:${javaGiVersion}")
    implementation("org.java-gi:gdkpixbuf:${javaGiVersion}")
    implementation("org.java-gi:gstreamer:${javaGiVersion}")
    implementation("ru.stersh:subsonic-api:${subsonicApiVersion}")
    implementation("io.ktor:ktor-client-apache5:${ktorVersion}")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")

    runtimeOnly("org.apache.logging.log4j:log4j-slf4j2-impl")
    runtimeOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

    testImplementation(platform("org.junit:junit-bom:${junitVersion}"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(commonJvmArgs)
}

application {
    applicationDefaultJvmArgs = commonJvmArgs
    mainClass.set("io.ibnuja.hypersonic.Hypersonic")
}
