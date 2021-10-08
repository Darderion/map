package com.github.darderion.mundaneassignmentpolice.wrapper

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.TextPosition
import java.io.*


class PDFListStripper: PDFTextStripper() {
	/**
	 * @throws IOException If there is an error parsing the document.
	 */
	fun main(args: Array<String>) {
		var document: PDDocument? = null
		val fileName = "apache.pdf"
		try {
			document = PDDocument.load(File(fileName))
			val stripper: PDFTextStripper = PDFListStripper()
			stripper.sortByPosition = true
			stripper.startPage = 0
			stripper.endPage = document!!.numberOfPages
			val dummy: Writer = OutputStreamWriter(ByteArrayOutputStream())
			stripper.writeText(document, dummy)
		} finally {
			document?.close()
		}
	}

	/**
	 * Override the default functionality of PDFTextStripper.writeString()
	 */
	override fun writeString(string: String?, textPositions: List<TextPosition>) {
		for (text: TextPosition in textPositions) {
			println(
				text.unicode + " [(X=" + text.xDirAdj + ",Y=" +
						text.yDirAdj + ") height=" + text.heightDir + " width=" +
						text.widthDirAdj + "]"
			)
		}
	}
}
