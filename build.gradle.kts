@file:Suppress("SpellCheckingInspection")

import org.apache.tools.ant.taskdefs.condition.Os

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

val msys64Dir = project.findProperty("msys64.dir") as? String ?: "C:/msys64/mingw64" //defaults to MINGW64
val msys64BinDir = "$msys64Dir/bin"

val commonJvmArgs = mutableListOf(
    "--enable-native-access=ALL-UNNAMED",
)

if (Os.isFamily(Os.FAMILY_WINDOWS)) {
    commonJvmArgs.add("-Djava.library.path=$msys64BinDir")
}

tasks.named<JavaExec>("run") {
    args(
        "Hypersonic",
        "src/main/java/io/ibnuja/Hypersonic.java",
        "src/main/java/io/ibnuja/HypersonicApp.java",
        "src/main/java/io/ibnuja/HypersonicMainWindow.java"
    )

    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
        environment("PATH", "$msys64BinDir;${System.getenv("PATH")}")
        val buildDataDir = layout.buildDirectory.get().asFile.absolutePath.replace('\\', '/')
        val msysDataDir = msys64Dir.replace('\\', '/')
        val pathSeparator = ";"
        val defaultDataDirs = "$msysDataDir/share${pathSeparator}/usr/local/share${pathSeparator}/usr/share"
        val existingDataDirs = System.getenv("XDG_DATA_DIRS") ?: defaultDataDirs
        environment("XDG_DATA_DIRS", "$buildDataDir$pathSeparator$existingDataDirs")
    }
}

tasks.register<Exec>("compileResources") {
    group = "build"
    description = "Compile GResource XML into a binary resource file."
    workingDir = file("src/main/resources")

    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
        val executablePath = "$msys64BinDir/glib-compile-resources.exe"
        commandLine = listOf(executablePath, "hypersonicapp.gresource.xml")
    } else {
        commandLine = listOf("glib-compile-resources", "hypersonicapp.gresource.xml")
    }
}

if (Os.isFamily(Os.FAMILY_WINDOWS)) {
    tasks.register<Copy>("copySchema") {
        group = "build"
        description = "Copy GSettings schema to build directory for compilation."
        from("src/main/resources") {
            include("*.gschema.xml")
        }
        into(layout.buildDirectory.dir("glib-2.0/schemas"))
    }

    tasks.register<Exec>("compileSchemas") {
        group = "build"
        description = "Compile GSettings schemas."

        dependsOn("copySchema")

        val schemaDirProvider = layout.buildDirectory.dir("glib-2.0/schemas")
        val schemaDir = schemaDirProvider.get().asFile

        inputs.dir(schemaDirProvider)
        outputs.file(schemaDirProvider.map { it.file("gschemas.compiled") })

        val executablePath = "$msys64BinDir/glib-compile-schemas.exe"
        commandLine = listOf(executablePath, schemaDir.absolutePath)
    }

    tasks.named("classes") {
        dependsOn("compileResources")
        dependsOn("compileSchemas")
    }
} else {
    tasks.named("classes") {
        dependsOn("compileResources")
    }
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

    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
        environment("PATH", "$msys64BinDir;${System.getenv("PATH")}")
        val buildDataDir = layout.buildDirectory.get().asFile.absolutePath.replace('\\', '/')
        val msysDataDir = msys64Dir.replace('\\', '/')
        val pathSeparator = ";"
        val defaultDataDirs = "$msysDataDir/share${pathSeparator}/usr/local/share${pathSeparator}/usr/share"
        val existingDataDirs = System.getenv("XDG_DATA_DIRS") ?: defaultDataDirs
        environment("XDG_DATA_DIRS", "$buildDataDir$pathSeparator$existingDataDirs")
    }
}

application {
    applicationDefaultJvmArgs = commonJvmArgs
    mainClass.set("io.ibnuja.Hypersonic")
}
