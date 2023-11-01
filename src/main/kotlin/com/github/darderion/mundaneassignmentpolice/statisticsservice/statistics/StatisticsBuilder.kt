package com.github.darderion.mundaneassignmentpolice.statisticsservice.statistics

import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFStructure
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Section
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import java.io.File
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min


class StatisticsBuilder
{
    private val MIN_TOTAL_SHARE_WORDS = 0.75
    private val fileStopWordsName="src/main/resources/StopWords.txt"

    fun getWordsStatistic(pdf : PDFDocument) : WordsStatistic
    {
        val stopWords : MutableList<String> =  mutableListOf()
        File(fileStopWordsName).forEachLine { stopWords.add(it) }

        var wordCount=0
        val allWords = hashMapOf<String, Int>()
        for (line in pdf.text.indices)
            for (wordInLine in 0 until pdf.text[line].text.size)
            {
                var word = pdf.text[line].text[wordInLine].text.trim()
                if (line!=0 && isSecondHalfOfWord(pdf.text[line-1])) {
                    continue
                }
                else if (word.isNotEmpty() && word!="")
                {
                    if (word.last()=='-') {
                        word = word.substring(0..word.length - 2) + pdf.text[line + 1].text[0].text
                    }
                    word = word
                        .replace(Regex("[0-9]"),"")                       //replace all numbers from a word
                        .replace(Regex("\u2014|\u2015|\u2012|\u2013"),"") //replace dashes from a word
                        .replace(Regex("""\.|,|:$|;|\?|\\!\*|%|\(|\)|\[|\\]|\u2774|\u2775|\u2997|\u2998<|>|="""), "")
                                                       //replace other most common special characters and punctuation marks
                                                       //not replacing / + № # @ ~ and quotes, arrows
                    if (word !in stopWords && word != "" && word.length>1 ) {

                        word = word.replaceFirstChar { it.uppercase() }
                        var check= false
                        for (lem in allWords.keys)
                        {
                            val border = floor( MIN_TOTAL_SHARE_WORDS* max(word.length, lem.length)).toInt()
                            if (border < min(word.length,lem.length)) {
                                for (i in 0..border) {
                                    if (word[i] == lem[i]) {
                                        check = true
                                        continue
                                    } else {
                                        check = false
                                        break
                                    }
                                }
                            }
                            if (check)
                            {
                                word=lem
                                break
                            }
                        }
                        if (allWords.containsKey(word)) {
                            var v = allWords.getValue(word)
                            v += 1
                            wordCount++
                            allWords[word] = v
                        } else {
                            wordCount++
                            allWords[word] = 1
                        }
                    }
                }
            }
        val topKWords = allWords.toList().sortedByDescending { (k, v) -> v }.toMap()
        return WordsStatistic(wordCount,topKWords)
    }
    fun getPageStatistic(pdfName: String,pdf: PDFDocument): PageStatistic {
        val pdfSections = PDFStructure(pdf.text).sections
        val pdfSize = PDFBox().getPDFSize(pdfName)
        val newpdfStructure: MutableList<Section> = mutableListOf()
        val statistic: MutableList<SectionStatistics> = mutableListOf()
        for (section in pdfSections)
            if (section.title[2].isDigit())
                continue
            else {
                newpdfStructure.add(section)
            }
        for (i in 0 until newpdfStructure.size) {
            var sectionSizeInPage: Int
            var sectionSizeInPercents: Float
            if (i != newpdfStructure.size - 1) {
                sectionSizeInPage =
                    pdf.text[newpdfStructure[i + 1].titleIndex].page - pdf.text[newpdfStructure[i].titleIndex].page
                sectionSizeInPercents = sectionSizeInPage / pdfSize.toFloat()
            } else {
                sectionSizeInPage = max(pdfSize - pdf.text[newpdfStructure[i].titleIndex].page, 1)
                sectionSizeInPercents = sectionSizeInPage / pdfSize.toFloat()
            }
            statistic.add(SectionStatistics(newpdfStructure[i], sectionSizeInPage, sectionSizeInPercents, pdfSize))
        }
        return PageStatistic(pdf.text.size, statistic)
    }

    private fun isSecondHalfOfWord(previousLine : Line): Boolean
    {
        return (previousLine.text.isNotEmpty()
                && previousLine.text[ previousLine.text.size-1 ].text!=""
                && previousLine.text[ previousLine.text.size-1 ].text.last()=='-')
    }

}