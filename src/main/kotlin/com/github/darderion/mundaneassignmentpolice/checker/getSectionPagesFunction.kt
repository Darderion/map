package com.github.darderion.mundaneassignmentpolice.checker

import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument

fun getPages(document: PDFDocument, word : String): Pair<Int,Int>
{
    var pages = -1 to -1
    var linesIndexes = -1 to -1
    val lines = document.text.filter {
        var isFirstSectionWithWord = true
        document.areas!!.sections.forEachIndexed { index , section ->
            if (isFirstSectionWithWord) {
                if (section.title.contains(word) && word != "Заключение") {
                    linesIndexes = section.contentIndex to document.areas.sections[index + 1].contentIndex
                    isFirstSectionWithWord = false
                }
                else if (section.title.contains(word)) {
                    linesIndexes = section.contentIndex to -1
                    isFirstSectionWithWord = false
                }
            }

        }
        if (word!="Заключение")
            linesIndexes.first <= it.documentIndex && it.documentIndex < linesIndexes.second
        else linesIndexes.first <= it.documentIndex
    }

    if (lines.isNotEmpty() && word!="Заключение")
        pages = lines[0].page to lines.last().page
    else if (lines.isNotEmpty())
        pages = lines[0].page to -1
    return pages
}