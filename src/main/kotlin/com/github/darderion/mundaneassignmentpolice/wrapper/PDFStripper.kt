package com.github.darderion.mundaneassignmentpolice.wrapper

import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Coordinate
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Font
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Symbol
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Word
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.TextPosition

class PDFStripper: PDFTextStripper() {
	val symbols: MutableList<Symbol> = mutableListOf()

	/**
	 * Override the default functionality of PDFTextStripper.writeString()
	 */
	override fun writeString(string: String?, textPositions: List<TextPosition>) {
		for (text: TextPosition in textPositions) {
			val symbol = text.unicode
			if (symbol != null && symbol != " ") {
				symbol.forEach {
					symbols.add(
						Symbol(it, Font(text.fontSize), Coordinate(text.xDirAdj to text.yDirAdj))
					)
				}
			}
			/*
			words.add(text.unicode + " [(X=" + text.xDirAdj + ",Y=" +
						text.yDirAdj + ") height=" + text.heightDir + " width=" +
						text.widthDirAdj + "]")
			 */
		}
	}
}
