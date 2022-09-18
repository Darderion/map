package com.github.darderion.mundaneassignmentpolice.utils

class ResourcesUtil {
    companion object {
        fun getResourceFileReader(path: String) =
            this::class.java.classLoader.getResourceAsStream(path).bufferedReader()

        fun getResourceText(path: String) = getResourceFileReader(path).readText()

        fun getResourceLines(path: String) = getResourceFileReader(path).readLines()
    }
}
