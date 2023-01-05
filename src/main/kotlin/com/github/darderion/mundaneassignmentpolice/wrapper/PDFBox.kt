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
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.io.read
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
					tables.forEach { table ->
						words = words.filter {
							if (isWordInTable(pageIndex, it, table)) {
								table.tableText.add(it)
								if (!(table.lineIndexes.contains(lineIndex)))
									table.lineIndexes.add(lineIndex)
							}
							!isWordInTable(pageIndex, it, table)
						}.toMutableList()
					}
					Line(line, pageIndex, lineIndex, words.toList())
			})
		}

		document.close()

		return PDFDocument(fileName, pdfText, tables, size.width.toDouble(), size.height.toDouble())
	}

	private fun isWordInTable(page: Int,word: Word, table: Table): Boolean{
		return page == table.page - 1 &&
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

	private fun getTables(path: String): List<Table>{
		ProcessBuilder("python3", "TableExtractionScript.py", "extraction", path).start()

		val fileName = path.replace("uploads/","")
		val tables = mutableListOf<Table>()

		File("uploads/tables/$fileName/").walkBottomUp().filter { it.isFile }.forEach {
			val df = DataFrame.read(it)
			tables.add(extractTable(df))
		}
		return tables
	}

	private val defaultPageHeight = 842.0

	private val pageTableIndex = 2
	private val x1TableIndex = 4
	private val y1TableIndex = 5
	private val x2TableIndex = 6
	private val y2TableIndex = 7
	private val rowTableIndex = 9
	private val colTableIndex = 11

	private fun extractTable(df: AnyFrame): Table{
		val indexTableInf = df.select{ cols(0) }.first { it[0] == "table information"}.index()
		val tableInf = df.select{cols(0)}.filter { it.index() >= indexTableInf }

		val page = tableInf[pageTableIndex][0].toString().toInt()
		val x1 = tableInf[x1TableIndex][0].toString().toDouble()
		val y1 = defaultPageHeight - tableInf[y1TableIndex][0].toString().toDouble()
		val x2 = tableInf[x2TableIndex][0].toString().toDouble()
		val y2 = defaultPageHeight - tableInf[y2TableIndex][0].toString().toDouble()
		val rowCount = tableInf[rowTableIndex][0].toString().toInt()
		val colCount = tableInf[colTableIndex][0].toString().toInt()
		val tableData  = df.filter { it.index()<indexTableInf }

		return Table(page, x1,y1,x2,y2,rowCount,colCount,tableData, mutableListOf(), mutableListOf())
	}
}
