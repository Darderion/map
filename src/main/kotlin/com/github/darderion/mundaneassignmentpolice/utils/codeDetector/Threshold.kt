package com.github.darderion.mundaneassignmentpolice.utils.codeDetector

import java.io.BufferedReader
import java.io.FileReader

/**
 * counts THRESHOLDs using real code samples from codeSample.txt
 */
class Threshold {
    companion object {
        private const val FILE =
            "src/main/kotlin/com/github/darderion/mundaneassignmentpolice/utils/codeDetector/codeSample.txt"
        var reader: BufferedReader? = null

        /**
         * FREQUENCY_THRESHOLD
         */
        private fun calculatingFrequencyThreshold(line: String): Double {
            val lineToList = Parser.parseString(line)
            val codeFrequency: Double
            val totalWords = lineToList.size.toDouble()
            val codeWords = CodeDetector.calculateCodeWords(line)
            codeFrequency = codeWords / totalWords
            return codeFrequency
        }

        private var FT_counter = 0
        private var FT_totalProb = 0.0
        private fun getFrequencyThreshold(): Double {
            try {
                reader = BufferedReader(FileReader(FILE))
                var line: String?

                while (reader!!.readLine().also { line = it } != null) {

                    if (line!!.isNotEmpty()) {
                        FT_totalProb += calculatingFrequencyThreshold(line!!)
                        FT_counter++
                    }

                }
            } catch (e: Exception) {
                println("An error occurred: ${e.message}")
            } finally {
                try {
                    reader?.close()
                } catch (e: Exception) {
                    println("An error occurred while closing the file: ${e.message}")
                }
            }
//            println(FT_totalProb / FT_counter.toDouble()) // can be useful for debugging
            return FT_totalProb / FT_counter.toDouble()
        }

        /**
         * PROPERTIES_THRESHOLD
         */
        private fun calculatingPropertiesThreshold(line: String): Double {
            val lineToList = Parser.parseString(line)
            val ptThreshold = CodeDetectorLineEvaluation(
                lineToList,
                lineToList.size.toDouble(),
                CodeDetector.calculateCodeWords(line)
            ).calculatePropertiesThreshold()
            return ptThreshold
        }

        private var PT_counter = 0
        private var PT_totalProb = 0.0
        private fun getPropertiesThreshold(): Double {
            try {
                reader = BufferedReader(FileReader(FILE))
                var line: String?

                while (reader!!.readLine().also { line = it } != null) {

                    if (line!!.isNotEmpty()) {
                        PT_totalProb += calculatingPropertiesThreshold(line!!)
                        PT_counter++
                    }

                }
            } catch (e: Exception) {
                println("An error occurred: ${e.message}")
            } finally {
                try {
                    reader?.close()
                } catch (e: Exception) {
                    println("An error occurred while closing the file: ${e.message}")
                }
            }
//            println(PT_totalProb / PT_counter.toDouble()) // can be useful for debugging
            return PT_totalProb / PT_counter.toDouble()
        }

        val FREQUENCY_THRESHOLD = getFrequencyThreshold()
        val PROPERTIES_THRESHOLD = getPropertiesThreshold()
    }

}

