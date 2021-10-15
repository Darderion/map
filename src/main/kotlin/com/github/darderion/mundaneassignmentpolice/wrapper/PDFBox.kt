package com.github.darderion.mundaneassignmentpolice.wrapper

import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.utils.imgToBase64String
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Font
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Symbol
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Text
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Word
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDResources
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.text.PDFTextStripper
import java.awt.image.RenderedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStreamWriter
import java.io.Writer

class PDFBox {
	val recentDocuments: HashMap<String, PDDocument> = hashMapOf()

	private fun getDocument(fileName: String): PDDocument {
		if (!recentDocuments.contains(fileName)) {
			recentDocuments[fileName] = PDDocument.load(File(fileName))
		}
		val document = recentDocuments[fileName]!!

		val accessPermission = document.currentAccessPermission
		if (!accessPermission.canExtractContent()) {
			throw Error("You do not have permission to open '$fileName' file")
		}

		return document
	}

	/**
	 * Returns html representation of pdf's text content
	 * @param fileName pdf's filename
	 * @return html text content
	 */
	fun getText(fileName: String) = getPDF(fileName).toHTMLString()

	/**
	 * Returns lines of pdf's text content
	 * @param fileName pdf's filename
	 * @return lines of text content
	 */
	fun getLines(fileName: String) = getPDF(fileName).text

	/**
	 * Returns PDFDocument object
	 * @param fileName pdf's filename
	 * @return PDFDocument
	 */
	fun getPDF(fileName: String): PDFDocument {
		val pdfText: MutableList<Text> = mutableListOf()

		val document = getDocument(fileName)
		val stripper = PDFStripper()			// Stripper with additional information
		val textStripper = PDFTextStripper()	// Text stripper

		val strippers = listOf(stripper, textStripper)

		for((lineIndex, pageIndex) in (0..document.pages.count).withIndex()) {
			// For each page
			strippers.forEach {
				it.startPage = pageIndex + 1
				it.endPage = it.startPage
			}

			val text = textStripper.getText(document) + '\n'

			stripper.symbols.clear()
			val dummy: Writer = OutputStreamWriter(ByteArrayOutputStream())
			stripper.writeText(document, dummy)

			var font: Font?
			var word: String
			var symb: Symbol
			val words: MutableList<Word> = mutableListOf()
			var contentIndex: Int
			var contentItem: String
			var coordinates: Pair<Float, Float> = Pair(0.0f, 0.0f)

			var stripperIndex = 0

			pdfText.addAll(text.split('\n').mapIndexed { line, content ->
				// For each line
				words.clear()
				word = ""
				font = null
				contentIndex = 0

				if (content.isNotEmpty()) {
					coordinates = stripper.symbols[stripperIndex].position
				}

				while (contentIndex < content.length) {
					// For each symbol
					contentItem = if (content.hasSurrogatePairAt(contentIndex)) {
						"${content[contentIndex]}${content[contentIndex + 1]}"
					} else {
						"${content[contentIndex]}"
					}
					contentIndex += contentItem.length

					if (contentItem == " ") {
						words.add(Word(word, font!!))
						words.add(Word.spaceCharacter)
						font = null
						word = ""
					} else {
						symb = stripper.symbols[stripperIndex]
						if (font == null) font = symb.font

						if (symb.font != font) {
							words.add(Word(word, font!!))
							font = symb.font
							word = symb.toString()
						} else {
							word += symb
						}
						stripperIndex++
					}
				}
				if (font == null && word.isEmpty()) font = Font(0.0f)
				words.add(Word(word, font!!))

				Text(line, pageIndex, lineIndex, words.toList(), coordinates)
			})
		}

		return PDFDocument(fileName, pdfText)
	}

	/**
	 * Returns images from PDF in base64 string format
	 * @param fileName pdf's filename
	 * @return ordered set of images in base64 string format
	 */
	fun getImages(fileName: String): LinkedHashSet<String> {
		val document = getDocument(fileName)

		val images: MutableList<RenderedImage> = ArrayList()
		for (page in document.pages) {
			images.addAll(getImagesFromResources(page.resources))
		}
		return linkedSetOf(*images.map(::imgToBase64String).toTypedArray())
	}

	private fun getImagesFromResources(resources: PDResources): List<RenderedImage> {
		val images: MutableList<RenderedImage> = ArrayList()
		for (xObjectName in resources.xObjectNames) {
			val xObject = resources.getXObject(xObjectName)
			if (xObject is PDFormXObject) {
				images.addAll(getImagesFromResources(xObject.resources))
			} else if (xObject is PDImageXObject) {
				images.add(xObject.image)
			}
		}
		return images
	}
}
