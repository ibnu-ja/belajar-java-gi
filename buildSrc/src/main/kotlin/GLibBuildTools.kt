import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Exec
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.getByType

class GLibBuildTools : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply(EnvironmentPlugin::class.java)

        val extension = project.extensions.create<GLibBuildToolsExtension>(
            "glibBuildTools",
            project
        )

        project.afterEvaluate {
            registerTasks(project, extension)
        }
    }

    private fun registerTasks(project: Project, extension: GLibBuildToolsExtension) {
        val env = project.extensions.getByType<EnvironmentExtension>()

        project.tasks.register<Exec>("compileBlueprints") {
            group = "build"
            description = "Compile Blueprint files into GtkBuilder XML"

            val resourceDir = extension.resourceDirectory.get()
            workingDir = project.file(resourceDir)

            val blueprintFiles = extension.blueprintFiles.get()
            val outputDir = extension.blueprintOutputDirectory.get()

            // CONDITIONAL: Skip task if no blueprint files are defined
            onlyIf {
                val hasFiles = blueprintFiles.isNotEmpty()
                if (!hasFiles) {
                    println("GLibBuildTools: No blueprint files found, skipping compilation.")
                }
                hasFiles
            }

            val args = mutableListOf(
                "blueprint-compiler",
                "batch-compile",
                outputDir,
                "."
            )
            args.addAll(blueprintFiles)

            args.addAll(extension.blueprintArgs.get())

            if (project.hasProperty("gtk.blueprintArgs")) {
                val extraArgs = project.property("gtk.blueprintArgs") as String
                args.addAll(extraArgs.split(" ").filter { it.isNotBlank() })
            }

            // Only configure command if we actually have files to process
            // (though onlyIf prevents execution, this prevents empty arg errors during config)
            if (blueprintFiles.isNotEmpty()) {
                executeCommand(env, *args.toTypedArray())
            }

            inputs.files(blueprintFiles.map { project.file("$resourceDir/$it") })
            outputs.dir(project.file("$resourceDir/$outputDir"))
        }

        project.tasks.register<Exec>("compileGResources") {
            group = "build"
            description = "Compile GResource XML into binary resource file"

            val resourceDir = extension.resourceDirectory.get()
            workingDir = project.file(resourceDir)
            dependsOn("compileBlueprints")

            val gresourceFile = extension.gresourceFile.get()

            val args = mutableListOf("glib-compile-resources", gresourceFile)
            args.addAll(extension.gresourceArgs.get())

            if (project.hasProperty("gtk.gresourceArgs")) {
                val extraArgs = project.property("gtk.gresourceArgs") as String
                args.addAll(extraArgs.split(" ").filter { it.isNotBlank() })
            }

            executeCommand(env, *args.toTypedArray())

            inputs.file(project.file("$resourceDir/$gresourceFile"))
            outputs.file(project.file("$resourceDir/${gresourceFile.replace(".xml", "")}"))
        }
    }
}

open class GLibBuildToolsExtension(private val project: Project) {
    val resourceDirectory: Property<String> = project.objects.property(String::class.java)
        .convention("src/main/resources")

    val blueprintFiles: ListProperty<String> = project.objects.listProperty(String::class.java)

    val blueprintOutputDirectory: Property<String> = project.objects.property(String::class.java)
        .convention("blueprint-compiler")

    val gresourceFile: Property<String> = project.objects.property(String::class.java)

    val blueprintArgs: ListProperty<String> = project.objects.listProperty(String::class.java)
    val gresourceArgs: ListProperty<String> = project.objects.listProperty(String::class.java)

    fun blueprints(vararg files: String) {
        blueprintFiles.addAll(*files)
    }

    fun blueprintArg(vararg args: String) {
        blueprintArgs.addAll(*args)
    }

    fun gresourceArg(vararg args: String) {
        gresourceArgs.addAll(*args)
    }
}
