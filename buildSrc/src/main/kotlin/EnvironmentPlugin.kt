import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create

class EnvironmentPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create<EnvironmentExtension>(
            "environment",
            project
        )

        project.afterEvaluate {
            configureEnvironment(project, extension)
        }
    }

    private fun configureEnvironment(project: Project, extension: EnvironmentExtension) {
        val os = System.getProperty("os.name").lowercase()

        if (!extension.type.isPresent) {
            extension.type.set(when {
                os.contains("windows") -> detectWindowsEnvironment()
                else -> EnvironmentType.NATIVE_POSIX
            })
        }

        if (!extension.installLocation.isPresent) {
            extension.installLocation.set(when (extension.type.get()) {
                EnvironmentType.NATIVE_POSIX -> InstallLocation.HOME_LOCAL
                EnvironmentType.MSYS2_MINGW64 -> InstallLocation.APPDATA_LOCAL
                // TODO: NATIVE_WINDOWS support
            })
        }

        val prefix = when (extension.installLocation.get()) {
            InstallLocation.HOME_LOCAL -> {
                when (extension.type.get()) {
                    EnvironmentType.MSYS2_MINGW64 -> {
                        val localAppData = System.getenv("LOCALAPPDATA") ?: "${System.getProperty("user.home")}/AppData/Local"
                        localAppData.replace("\\", "/")
                    }
                    EnvironmentType.NATIVE_POSIX -> "${System.getProperty("user.home")}/.local"
                    // TODO: NATIVE_WINDOWS support
                }
            }
            InstallLocation.SYSTEM -> {
                when (extension.type.get()) {
                    EnvironmentType.MSYS2_MINGW64 -> {
                        val msys2Home = System.getenv("MSYS2_HOME") ?: "C:\\msys64"
                        msys2Home.replace("\\", "/") + "/mingw64"
                    }
                    EnvironmentType.NATIVE_POSIX -> "/usr/local"
                    // TODO: NATIVE_WINDOWS support
                }
            }
            InstallLocation.BUILD_DIR -> {
                project.buildDir.absolutePath.replace("\\", "/") + "/install"
            }
            InstallLocation.APPDATA_LOCAL -> {
                val localAppData = System.getenv("LOCALAPPDATA") ?: "${System.getProperty("user.home")}/AppData/Local"
                localAppData.replace("\\", "/")
            }
            // TODO: CUSTOM support
        }

        extension.prefix.set(prefix)

        val libPath = when (extension.type.get()) {
            EnvironmentType.MSYS2_MINGW64 -> {
                val msys2Home = System.getenv("MSYS2_HOME") ?: "C:\\msys64"
                "$msys2Home/mingw64/bin"
            }
            EnvironmentType.NATIVE_POSIX -> "$prefix/lib"
            // TODO: NATIVE_WINDOWS support
        }

        extension.libraryPath.set(libPath)
    }

    private fun detectWindowsEnvironment(): EnvironmentType {
        val msys2Home = System.getenv("MSYS2_HOME") ?: "C:\\msys64"
        val msys2Exists = java.io.File(msys2Home).exists()

        return if (msys2Exists) {
            EnvironmentType.MSYS2_MINGW64
        } else {
            // TODO: Detect NATIVE_WINDOWS (gvsbuild/manual)
            EnvironmentType.MSYS2_MINGW64
        }
    }
}

open class EnvironmentExtension(private val project: Project) {
    val type: Property<EnvironmentType> = project.objects.property(EnvironmentType::class.java)
    val installLocation: Property<InstallLocation> = project.objects.property(InstallLocation::class.java)

    val prefix: Property<String> = project.objects.property(String::class.java)
    val libraryPath: Property<String> = project.objects.property(String::class.java)

    fun isPosixCompliant(): Boolean {
        return type.get() in listOf(EnvironmentType.MSYS2_MINGW64, EnvironmentType.NATIVE_POSIX)
    }
}

enum class EnvironmentType {
    NATIVE_POSIX,
    MSYS2_MINGW64,
    // TODO: NATIVE_WINDOWS
}

enum class InstallLocation {
    HOME_LOCAL,
    SYSTEM,
    BUILD_DIR,
    APPDATA_LOCAL,
    // TODO: CUSTOM
}
