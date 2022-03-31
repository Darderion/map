package com.github.darderion.mundaneassignmentpolice.pdfdocument

import com.github.darderion.mundaneassignmentpolice.checker.statistics.*
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Section
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import mu.KotlinLogging
import java.lang.Exception

class PDFDocument(val name: String = "PDF",
				  val text: List<Line>,
				  val width: Double = defaultPageWidth,
				  val height: Double = defaultPageHeight
				  ) {
	override fun toString() = "PDF: $name\n" +
			text.joinToString("\n") { it.toString() }

	val areas: PDFStructure? = try {
		PDFStructure(text)
	} catch (e: Exception) {
		logger.error(e.message)
		null
	} catch (e: Error) {
		logger.error(e.message)
		null
	}

	fun toHTMLString() = text.joinToString("<br>") { it.content }

	fun getTextFromLines(fromIndex: Int, toIndex: Int, region: PDFRegion) = text.filter { it.area!! inside region }
		.filterIndexed { index, pdfText ->
		index in fromIndex..toIndex
	}.joinToString("\n ") { it.content }

	fun print() {
		text.map { "${it.area} | ${it.text.joinToString("--") { "${it.font.size}-${it.text}"}}" }.forEach(::println)
	}
	fun getWordsStatistic() : WordsStatistic
	{
		var wordCount=0
		val allWords = hashMapOf<String, Int>()
		for (line in text.indices)
			for (wordInLine in 0 until text[line].text.size)
			{
				var word = text[line].text[wordInLine].text
				if (line!=0 && text[line - 1].text.isNotEmpty()
					&& text[line-1].text[ text[line-1].text.size-1 ].text!=""
					&& text[line-1].text[ text[line-1].text.size-1 ].text.last()=='-') {
					continue
				}
				else if (word.isNotEmpty() && word!="" && word!=" ")
				{
					if (word.last()=='-') {
						word = word.substring(0..word.length - 2) + text[line + 1].text[0].text
					}
					word = word.replace(Regex("/[^а-яёА-ЯЁa-zA-Z+#]"), "")
						.replace(Regex("[0-9]"),"")
						.replace(Regex("\u2014|\u2015|\u2012|\u2013"),"")
						.replace(Regex("""^\(|\)|\.|,$|:$|;$|\?$|\*|^\[|\\]$"""), "")

					if (word !in wordsEN && word !in wordsRU && word != "" && word.length>1 ) {

						word = word.replaceFirstChar { it.uppercase() }
						var check= false
						for (lem in allWords.keys)
						{
							val border = kotlin.math.floor(0.75 * kotlin.math.max(word.length, lem.length)).toInt()
							if (border < kotlin.math.min(word.length,lem.length)) {
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
	fun getPageStatistic(): PageStatistic {
		val pdfsections = PDFStructure(text).sections
		val pdfSize = PDFBox().getPDFSize(name)
		val newpdfStructure: MutableList<Section> = mutableListOf()
		val Statistic: MutableList<SectionStatistics> = mutableListOf()
		for (section in pdfsections)
			if (section.title[2].isDigit())
				continue
			else {
				newpdfStructure.add(section)
			}
		for (i in 0 until newpdfStructure.size) {
			var sectionSizeInPage: Int
			var sectionSizeInPercents: Float
			val documentSize = pdfSize
			if (i != newpdfStructure.size - 1) {
				sectionSizeInPage =
					text[newpdfStructure[i + 1].titleIndex].page - text[newpdfStructure[i].titleIndex].page
				sectionSizeInPercents =
					(text[newpdfStructure[i + 1].titleIndex].page - text[newpdfStructure[i].titleIndex].page) / pdfSize.toFloat()
			} else {
				sectionSizeInPage = kotlin.math.max(pdfSize - text[newpdfStructure[i].titleIndex].page, 1)
				sectionSizeInPercents =
					kotlin.math.max(pdfSize - text[newpdfStructure[i].titleIndex].page, 1) / pdfSize.toFloat()
			}
			Statistic.add(SectionStatistics(newpdfStructure[i], sectionSizeInPage, sectionSizeInPercents, documentSize))
		}
		return PageStatistic(text.size, Statistic)
	}


	private companion object {
		private const val defaultPageHeight = 842.0
		private const val defaultPageWidth = 595.22

		private val logger = KotlinLogging.logger {}
	}
}
