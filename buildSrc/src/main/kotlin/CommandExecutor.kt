import org.gradle.api.tasks.Exec

fun Exec.executeCommand(env: EnvironmentExtension, vararg command: String) {
    val fullCommand = command.joinToString(" ")

    when (env.type.get()) {
        EnvironmentType.MSYS2_MINGW64 -> {
            val msysPath = System.getenv("MSYS2_HOME") ?: "C:\\msys64"
            environment("MSYSTEM", "MINGW64")
            val escapedDir = workingDir.absolutePath.replace("\\", "/")

            val bashCommand = "cd \"$escapedDir\" && $fullCommand"

            println("Executing (MSYS2): $bashCommand")

            commandLine(
                "$msysPath\\usr\\bin\\bash.exe",
                "-lc",
                bashCommand
            )
        }
        EnvironmentType.NATIVE_POSIX -> {
            println("Executing (POSIX): $fullCommand")
            println("Working directory: ${workingDir.absolutePath}")

            commandLine(*command)
        }
        // TODO: NATIVE_WINDOWS support
    }
}
