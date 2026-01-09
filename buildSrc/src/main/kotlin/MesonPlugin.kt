import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Exec
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.getByType

class MesonPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply(EnvironmentPlugin::class.java)

        val extension = project.extensions.create<MesonExtension>(
            "meson",
            project
        )

        project.afterEvaluate {
            registerTasks(project, extension)
        }
    }

    private fun registerTasks(project: Project, extension: MesonExtension) {
        val env = project.extensions.getByType<EnvironmentExtension>()

        project.tasks.register<Exec>("mesonSetup") {
            group = "meson"
            description = "Configure Meson build"

            val buildDir = extension.buildDirectory.get()
            val sourceDir = extension.sourceDirectory.get()
            val buildType = extension.buildType.get()
            val prefix = extension.prefix.getOrElse(env.prefix.get())

            val options = mutableListOf(
                "--prefix=$prefix",
                "--buildtype=$buildType"
            )

            extension.options.get().forEach { (key, value) ->
                options.add("-D$key=$value")
            }

            options.addAll(extension.setupArgs.get())

            if (project.hasProperty("mesonArgs")) {
                val args = project.property("mesonArgs") as String
                options.addAll(args.split(" ").filter { it.isNotBlank() })
            }

            executeCommand(
                env,
                "meson", "setup",
                buildDir,
                sourceDir,
                *options.toTypedArray()
            )

            val buildDirFile = project.file(buildDir)
            outputs.dir(buildDirFile)
            outputs.upToDateWhen { buildDirFile.exists() && buildDirFile.resolve("build.ninja").exists() }
        }

        project.tasks.register<Exec>("mesonCompile") {
            group = "meson"
            description = "Compile Meson project"
            dependsOn("mesonSetup")

            val buildDir = extension.buildDirectory.get()
            workingDir = project.file(buildDir)

            val args = mutableListOf<String>()
            args.addAll(extension.compileArgs.get())

            if (project.hasProperty("mesonArgs")) {
                val extraArgs = project.property("mesonArgs") as String
                args.addAll(extraArgs.split(" ").filter { it.isNotBlank() })
            }

            executeCommand(env, "meson", "compile", *args.toTypedArray())
        }

        project.tasks.register<Exec>("mesonInstall") {
            group = "meson"
            description = "Install Meson project"
            dependsOn("mesonCompile")

            val buildDir = extension.buildDirectory.get()
            workingDir = project.file(buildDir)

            val args = mutableListOf<String>()
            args.addAll(extension.installArgs.get())

            if (project.hasProperty("mesonArgs")) {
                val extraArgs = project.property("mesonArgs") as String
                args.addAll(extraArgs.split(" ").filter { it.isNotBlank() })
            }

            executeCommand(env, "meson", "install", *args.toTypedArray())
        }

        project.tasks.register<Exec>("mesonTest") {
            group = "meson"
            description = "Run Meson tests"
            dependsOn("mesonCompile")

            val buildDir = extension.buildDirectory.get()
            workingDir = project.file(buildDir)

            val args = mutableListOf<String>()
            args.addAll(extension.testArgs.get())

            if (project.hasProperty("mesonArgs")) {
                val extraArgs = project.property("mesonArgs") as String
                args.addAll(extraArgs.split(" ").filter { it.isNotBlank() })
            }

            executeCommand(env, "meson", "test", *args.toTypedArray())
        }

        project.tasks.register<Exec>("mesonClean") {
            group = "meson"
            description = "Clean Meson build"

            val buildDir = extension.buildDirectory.get()

            doLast {
                project.file(buildDir).deleteRecursively()
            }
        }
    }
}

open class MesonExtension(private val project: Project) {
    val buildDirectory: Property<String> = project.objects.property(String::class.java)
        .convention("build/meson")

    val sourceDirectory: Property<String> = project.objects.property(String::class.java)
        .convention(".")

    val prefix: Property<String> = project.objects.property(String::class.java)

    val buildType: Property<String> = project.objects.property(String::class.java)
        .convention("release")

    val options: MapProperty<String, String> = project.objects.mapProperty(String::class.java, String::class.java)

    val setupArgs: ListProperty<String> = project.objects.listProperty(String::class.java)
    val compileArgs: ListProperty<String> = project.objects.listProperty(String::class.java)
    val installArgs: ListProperty<String> = project.objects.listProperty(String::class.java)
    val testArgs: ListProperty<String> = project.objects.listProperty(String::class.java)

    fun option(key: String, value: String) {
        options.put(key, value)
    }

    fun setupArg(vararg args: String) {
        setupArgs.addAll(*args)
    }

    fun compileArg(vararg args: String) {
        compileArgs.addAll(*args)
    }

    fun installArg(vararg args: String) {
        installArgs.addAll(*args)
    }

    fun testArg(vararg args: String) {
        testArgs.addAll(*args)
    }
}
