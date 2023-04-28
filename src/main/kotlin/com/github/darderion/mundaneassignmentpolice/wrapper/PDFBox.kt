package com.github.darderion.mundaneassignmentpolice.wrapper

import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.tables.Table
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
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.read
import java.awt.Color
import java.awt.image.RenderedImage
import java.io.*
import java.nio.file.Files
import java.nio.file.LinkOption
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashSet
import kotlin.io.path.Path


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
		val tables = getTables(fileName)

		val pdfText: MutableList<Line> = mutableListOf()

		val document = getDocument(fileName)
		val stripper = PDFStripper()			// Stripper with additional information
		val textStripper = PDFTextStripper()	// Text stripper

		val size = document.pages.first().mediaBox

		val strippers = listOf(stripper, textStripper)

		var lineIndex = -1
		for (pageIndex in (0 until document.pages.count)) {
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
			var words: MutableList<Word> = mutableListOf()
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

				tables.filter { table -> table.page == pageIndex  }.forEach { table ->
					words = words.filter { word -> !isWordInTable(pageIndex, word, table) }
						.filter { it.text.isNotEmpty() }.toMutableList()
				}

				if (document.pages[pageIndex].resources.xObjectNames.count() != 0){
					Line(line, pageIndex, lineIndex, words.toList(),null,Coordinate(0,0))
				}
				else{
					Line(line, pageIndex, lineIndex, words.toList(),null,stripper.symbols[stripperIndex-1].position)}
			}
			)

			var line = text.lines().size
			tables.forEach { table ->
				if (table.page == pageIndex)
					table.cells.forEach { cell ->
						val cellLines = mutableListOf<Line>()
						cellLines.addAll(cell.cellText.filter { it.isNotEmpty() }.map { content ->
							words.clear()
							content.split(" ").forEach {
								words.add(Word(it, Font(12f), cell.leftCorner))
							}
							lineIndex += 1
							line += 1
							val tableLine = Line(line, pageIndex, lineIndex, words.toList(),
								endPosition = Coordinate(cell.rightCorner.x, cell.rightCorner.y))
							cell.cellLines = cellLines
							pdfText.add(tableLine)
							tableLine
						}

						)
					}
			}
		}
		document.close()

		return PDFDocument(fileName, pdfText, tables, size.width.toDouble(), size.height.toDouble())
	}

	private fun isWordInTable(page: Int, word: Word, table: Table): Boolean {
		return page == table.page &&
				word.position.x >= table.x1 && word.position.y <= table.y1 &&
				word.position.x <= table.x2 && word.position.y >= table.y2
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

	/**
	 * Returns tables from PDF
	 * @param path pdf's path
	 * @return list of Table
	 */
	fun getTables(path: String): List<Table>{

		val workingDirPath = System.getProperty("user.home") + "/map"
		val fileName = path.replace("uploads/","")
		val tables = mutableListOf<Table>()

		if (!Files.exists(Path("$workingDirPath/uploads/tables/$fileName"), LinkOption.NOFOLLOW_LINKS)) {

			ProcessBuilder(
				"src/main/python/venv/bin/python3",
				"src/main/python/TableExtractionScript.py",
				"extraction", path
			)
				.directory(File(workingDirPath))
				.redirectOutput(ProcessBuilder.Redirect.INHERIT)
				.start()
				.waitFor()
		}

		File("$workingDirPath/uploads/tables/$fileName/").walkBottomUp().filter { it.isFile }.forEach {
			val df = DataFrame.read(it)
			tables.add(Table(df))
		}

		return tables
	}

}
