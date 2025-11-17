@file:Suppress("SpellCheckingInspection")

plugins {
    id("application")
    id("io.freefair.lombok") version "9.0.0"
}

group = "io.ibnuja"
version = "1.0-SNAPSHOT"

val slf4jVersion = "2.0.17"
val log4jVersion = "2.25.2"
val junitVersion = "5.10.0"
val jacksonBomVersion = "2.20.0"
val javaGiVersion = "0.12.2"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

val commonJvmArgs = mutableListOf(
    "--enable-native-access=ALL-UNNAMED",
)

tasks.named<JavaExec>("run") {
    dependsOn("compileResources")
    args(
        "Hypersonic",
    )
}

tasks.register("createBlueprintDir") {
    group = "build"
    description = "Creates the directory for compiled blueprint files."
    doLast {
        file("src/main/resources/blueprint-compiler/components/settings").mkdirs()
        file("src/main/resources/blueprint-compiler/components/sidebar").mkdirs()
    }
}

tasks.register<Exec>("compileBlueprints") {
    group = "build"
    description = "Compile Blueprint files into GtkBuilder XML."
    workingDir = file("src/main/resources")
    dependsOn("createBlueprintDir")

    val inputFiles = listOf(
        "window.blp",
        "components/settings/settings.blp",
        "components/sidebar/sidebar.blp"
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
    implementation("io.github.jwharm.javagi:gtk:${javaGiVersion}")
    implementation("io.github.jwharm.javagi:adw:${javaGiVersion}")

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
