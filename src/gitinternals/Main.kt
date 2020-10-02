package gitinternals

import java.io.File
import java.io.FileInputStream
import java.util.zip.InflaterInputStream


fun main() {
    println("Enter .git directory location:")
    val gitFolderLocation = readLine()!!
    println("Enter git object hash:")
    val gitObjectHash = readLine()!!
    val file = findFileWithObject(gitFolderLocation, gitObjectHash)
    printGitObject(file)
}

fun findFileWithObject(gitFolderLocation: String, gitObjectHash: String): File? {
    return File(gitFolderLocation)
            .walkTopDown()
            .associateBy { file -> file.absolutePath }
            .entries.find { e ->
                e.key.replace("\\", "")
                        .contains(gitObjectHash)
            }?.value
}

fun printGitObject(file: File?) {
    if (file == null) return

    val nullChar = 0.toChar()
    val fileInputStream = FileInputStream(file)
    val inflaterInputStream = InflaterInputStream(fileInputStream)

    inflaterInputStream.use {
        while (inflaterInputStream.available() > 0) {
            // TODO: debug this, throws java.io.EOFException: Unexpected end of ZLIB input stream
            val charFromStream = inflaterInputStream.read().toChar()
            val charToPrint = if (charFromStream == nullChar) "\n" else charFromStream
            print(charToPrint)
        }
    }
}
