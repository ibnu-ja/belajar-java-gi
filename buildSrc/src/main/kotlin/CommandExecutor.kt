import org.gradle.api.tasks.Exec

fun Exec.executeCommand(env: EnvironmentExtension, vararg command: String) {
    val fullCommand = command.joinToString(" ")

    when (env.type.get()) {
        EnvironmentType.MSYS2_MINGW64 -> {
            val msysPath = System.getenv("MSYS2_HOME") ?: "C:\\msys64"
            environment("MSYSTEM", "MINGW64")
            val escapedDir = workingDir.absolutePath.replace("\\", "/")

            val bashCommand = "cd \"$escapedDir\" && $fullCommand"

            logger.lifecycle("Executing (MSYS2): $bashCommand")

            commandLine(
                "$msysPath\\usr\\bin\\bash.exe",
                "-lc",
                bashCommand
            )
        }
        EnvironmentType.NATIVE_POSIX -> {
            logger.lifecycle("Executing (POSIX): $fullCommand")
            logger.lifecycle("Working directory: ${workingDir.absolutePath}")

            commandLine(*command)
        }
    }
}
