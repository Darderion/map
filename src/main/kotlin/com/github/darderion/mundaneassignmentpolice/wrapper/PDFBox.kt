package com.github.darderion.mundaneassignmentpolice.wrapper

import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.*
import com.github.darderion.mundaneassignmentpolice.utils.imgToBase64String
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.PDResources
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.text.PDFTextStripper
import java.awt.Color
import java.awt.image.RenderedImage
import java.io.*


class PDFBox {
	val recentDocuments: HashMap<String, PDDocument> = hashMapOf()
	val recentDocumentsSizes: HashMap<String, Int> = hashMapOf()

	fun getDocument(fileName: String): PDDocument {
		/*
		if (!recentDocuments.contains(fileName)) {
			recentDocuments[fileName] = PDDocument.load(File(fileName))
		}
		val document = recentDocuments[fileName]!!
		 */

		val document = PDDocument.load(File(fileName))

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

	private fun editPDF(document: PDDocument, page: Int, functionBody: (contentStream: PDPageContentStream) -> Unit): PDDocument {
		try {
			val contentStream = PDPageContentStream(
				document,
				document.documentCatalog.pages[page],
				PDPageContentStream.AppendMode.APPEND,
				true
			)
			functionBody(contentStream)
			contentStream.close()
		} catch (e: IOException) {
			e.printStackTrace()
		}
		return document
	}

	fun addText(document: PDDocument, page: Int, text: String, position: Coordinate) = editPDF(document, page) {
		val font: PDFont = PDType1Font.HELVETICA_BOLD
		it.beginText()
		it.setFont(font, 12f)
		it.moveTextPositionByAmount(position.x, position.y)
		it.drawString(text)
		it.endText()
	}

	fun addLine(document: PDDocument, page: Int, position: Coordinate, width: Int) = editPDF(document, page) {
		it.moveTo(position.x, position.y)
		it.lineTo(position.x + width, position.y)
		it.setStrokingColor(Color.RED)
		it.stroke()
	}

	/**
	 * Returns PDFDocument object
	 * @param fileName pdf's filename
	 * @return PDFDocument
	 */
	fun getPDF(fileName: String): PDFDocument {
		val pdfText: MutableList<Line> = mutableListOf()

		val document = getDocument(fileName)
		val stripper = PDFStripper()			// Stripper with additional information
		val textStripper = PDFTextStripper()	// Text stripper

		val numberOfPages = document.pages.count
		val size = document.pages.first().mediaBox

		val strippers = listOf(stripper, textStripper)

		var lineIndex = -1
		for (pageIndex in (0 until numberOfPages)) {
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
			var coordinates = Coordinate(0, 0)
			var stripperIndex = 0

			pdfText.addAll(text.lines().mapIndexed { line, content ->
				// For each line
				lineIndex++
				words.clear()
				word = ""
				font = null
				contentIndex = 0

				while (contentIndex < content.length) {
					// For each symbol
					contentItem = if (content.hasSurrogatePairAt(contentIndex)) {
						"${content[contentIndex]}${content[contentIndex + 1]}"
					} else {
						"${content[contentIndex]}"
					}
					contentIndex += contentItem.length

					if (contentItem == " ") {
						words.add(Word(word, font?: Font(0.0f), coordinates))
						words.add(Word.spaceCharacter)
						font = null
						word = ""
					} else {
						symb = stripper.symbols[stripperIndex]
						if (font == null) font = symb.font

						if (symb.font != font) {
							words.add(Word(word, font!!, coordinates))
							font = symb.font
							word = symb.toString()
						} else {
							if (word.isEmpty()) {
								coordinates = stripper.symbols[stripperIndex].position
							}
							word += symb
						}
						stripperIndex++
					}
				}
				if (font == null && word.isEmpty()) font = Font(0.0f)
				words.add(Word(word, font!!, coordinates))

				if (document.pages[pageIndex].resources.xObjectNames.count()!=0){
					Line(line, pageIndex, lineIndex, words.toList(),null,Coordinate(0,0))
				}
				else{
				if (words.isEmpty()){
					Line(line, pageIndex, lineIndex, words.toList(),null,stripper.symbols[stripperIndex].position)
				}
				else{
				Line(line, pageIndex, lineIndex, words.toList(),null,stripper.symbols[stripperIndex-1].position)}}
			})
		}

		document.close()

		return PDFDocument(fileName, pdfText, numberOfPages, size.width.toDouble(), size.height.toDouble())
	}

	fun getPDFSize(fileName: String): Int {
		if (!recentDocumentsSizes.contains(fileName)) {
			recentDocumentsSizes[fileName] = PDDocument.load(File(fileName)).numberOfPages
		}
		return recentDocumentsSizes[fileName]!!
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
