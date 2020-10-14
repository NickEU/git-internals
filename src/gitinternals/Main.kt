package gitinternals

import java.io.File
import java.io.FileInputStream
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.zip.InflaterInputStream


fun main() {
    println("Enter .git directory location:")
    val gitFolderLocation = readLine()!!
    println("Enter git object hash:")
    val gitObjectHash = readLine()!!
    val file = findFileWithObject(gitFolderLocation, gitObjectHash)
    val rawObjectData = readGitObject(file)
    val formattedObjectData = parseObject(rawObjectData)
    println(formattedObjectData)
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

fun readGitObject(file: File?): String {
    if (file == null) return ""

    val nullChar = 0.toChar()
    val fileInputStream = FileInputStream(file)
    val inflaterInputStream = InflaterInputStream(fileInputStream)
    var result = ""
    inflaterInputStream.use {
        while (inflaterInputStream.available() > 0) {
            val charFromStream = inflaterInputStream.read().toChar()
            result += if (charFromStream == nullChar) "\n" else charFromStream
        }
    }
    return result
}

fun parseObject(rawText: String) {
    val lines = rawText.split("\n")
    for (line in lines) {
        println(line)
    }
    when (lines[0].split(" ")[0]) {
        "commit" -> parseCommit(lines)
//        "blob" -> parseBlob(lines)
//        "tree" -> parseTree(lines)
    }
}

fun parseCommit(lines: List<String>) {
    println(lines.size)
    println("*COMMIT*")
    println(lines[1].replace(" ", ": "))
    println(lines[2].replace("parent", "parents:"))
    val authorLine = lines[3].split(" ")
    println(parseAuthorCommitter(authorLine, true))
    val committerLine = lines[4].split(" ")
    println(parseAuthorCommitter(committerLine, false))
////    val authorName: String =
////    val authorEmail: String =
//    val originalDate: Instant = Instant.ofEpochSecond(lines[9].toLong())
//    val originalDateTZ: String = lines[10]
//    val committerName: String = lines[12]
//    val committerEmail: String = lines[13]
//    val commitDate: Instant = Instant.ofEpochSecond(lines[14].toLong())
//    val commitDateTZ: String = lines[15]
//    val commitMsg: String = lines.subList(17, lines.size).joinToString(" ")
}

fun parseAuthorCommitter(parsedLine: List<String>, isAuthor: Boolean): String {
    val timestampType = if (isAuthor) "original" else "commit"
    val infoType = if (isAuthor) "author" else "committer"
    val personNameEmail = "$infoType: " + parsedLine.subList(1, 3)
            .joinToString(" ")
            .replace(Regex("[<>]"), "")
    val originalDate: Instant = Instant.ofEpochSecond(parsedLine[3].toLong())
    //DateTimeFormatter.ISO_INSTANT.
    return "$personNameEmail $timestampType timestamp: " +
            "${DateTimeFormatter.ISO_INSTANT.format(originalDate)
                    .replace(Regex("[TZ]"), " ")}${parsedLine.last()}"
}

fun printHeader(header: String) {
    val headerList = header.split(" ")
    println("type:${headerList[0]} length:${headerList[1]}")
}
