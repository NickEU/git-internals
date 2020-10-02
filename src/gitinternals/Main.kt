package gitinternals

import java.io.FileInputStream
import java.util.zip.InflaterInputStream


fun main() {
    println("Enter git object location:")
    val fileLocation = readLine()!!
    printGitObject(fileLocation)
}

fun printGitObject(fileLocation: String) {
    val nullChar = 0.toChar()
    val fileInputStream = FileInputStream(fileLocation)
    val inflaterInputStream = InflaterInputStream(fileInputStream)

    inflaterInputStream.use {
        while (inflaterInputStream.available() > 0) {
            val charFromStream = inflaterInputStream.read().toChar()
            val charToPrint = if (charFromStream == nullChar) "\n" else charFromStream
            print(charToPrint)
        }
    }
}
