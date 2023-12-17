package com.github.darderion.mundaneassignmentpolice.utils.codeDetector

import java.io.BufferedReader
import java.io.FileReader


class Threshold {
    companion object {
        private const val FILE = "src/main/kotlin/com/github/darderion/mundaneassignmentpolice/utils/codeDetector/codeSample.txt"
        var reader: BufferedReader? = null

        private var counter = 0
        private var totalProb = 0.0

        private fun countingFrequencyThreshold(line:String): Double {
            val lineToList = CodeDetector.splitIntoList(line)
            val codeFrequency: Double
            val totalWords = lineToList.size
            var codeWords = 0
            if (CodeDetectorDB.can_start_with.contains(lineToList[0])) {
//                codeWords++
                return 1.0
            }
            codeWords += lineToList.filter { CodeDetectorDB.codePatterns.contains(it) || CodeDetectorDB.delimiters.contains(it) || CodeDetectorDB.can_start_with.contains(it) || CodeDetectorDB.can_be_the_only_element.contains(it) }.size
//            if (line.startsWith("    ") || line.startsWith("\t")) codeWords++
            for (word in lineToList) {
                try {
                    java.lang.Double.parseDouble(word)
                    codeWords++
                } catch (e: NumberFormatException) {
                    continue
                }
            }
            codeFrequency = (codeWords / totalWords).toDouble()
            return codeFrequency
        }

        private fun countingPropertiesThreshold(line: String): Double {
            val lineToList = CodeDetector.splitIntoList(line)
            TODO()
        }

        private fun getFrequencyThreshold(): Double {
            try {
                reader = BufferedReader(FileReader(FILE))
                var line: String?

                while (reader!!.readLine().also { line = it } != null) {

                    if (line!!.isNotEmpty()) {
                        totalProb += countingFrequencyThreshold(line!!)
                        counter++
                    }

                }
                //        println(totalProb / counter)
            } catch (e: Exception) {
                println("An error occurred: ${e.message}")
            } finally {
                try {
                    reader?.close()
                } catch (e: Exception) {
                    println("An error occurred while closing the file: ${e.message}")
                }
            }
            println(totalProb/ counter)
            return totalProb/ counter
        }

        private fun getPropertiesThreshold(): Double {
            TODO()
        }

        val FREQUENCY_THRESHOLD = getFrequencyThreshold()
        val PROPERTIES_THRESHOLD = getPropertiesThreshold()
    }

}

