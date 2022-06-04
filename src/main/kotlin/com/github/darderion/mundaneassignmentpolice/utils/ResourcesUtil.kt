package com.github.darderion.mundaneassignmentpolice.utils

class ResourcesUtil {
    companion object {
        fun getResourceText(path: String) = this::class.java.classLoader.getResource(path).readText()
    }
}
