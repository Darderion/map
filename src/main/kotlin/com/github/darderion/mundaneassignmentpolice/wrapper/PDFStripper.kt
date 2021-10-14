package com.github.darderion.mundaneassignmentpolice.wrapper

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
				symbols.add(
					Symbol(symbol, Font(text.fontSize), text.xDirAdj to text.yDirAdj)
				)
			}
			/*
			words.add(text.unicode + " [(X=" + text.xDirAdj + ",Y=" +
						text.yDirAdj + ") height=" + text.heightDir + " width=" +
						text.widthDirAdj + "]")
			 */
		}
	}
}
