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
    buildGitObject(file)
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

fun buildGitObject(file: File?) {
    if (file == null) return

    val nullChar = 0.toChar()
    val fileInputStream = FileInputStream(file)
    val inflaterInputStream = InflaterInputStream(fileInputStream)
    var result = ""
    inflaterInputStream.use {
        while (inflaterInputStream.available() > 0) {
            val charFromStream = inflaterInputStream.read().toChar()
            // TODO: prettify this
            val charToPrint = if (charFromStream == nullChar) "\n" else charFromStream
            result += charToPrint
            if (charToPrint == "\n") {
                printHeader(result)
                break
            }
        }
    }
}

fun printHeader(header: String) {
    val headerList = header.split(" ")
    println("type:${headerList[0]} length:${headerList[1]}")
}
