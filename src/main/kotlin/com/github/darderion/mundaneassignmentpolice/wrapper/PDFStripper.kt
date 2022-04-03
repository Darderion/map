package com.github.darderion.mundaneassignmentpolice.wrapper

import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Coordinate
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Font
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.PostScriptFontType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Symbol
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.apache.pdfbox.pdmodel.font.PDType1CFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.PDType3Font
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

			val fontType = when (text.font) {
				is PDType0Font -> PostScriptFontType.TYPE0
				is PDType1Font -> PostScriptFontType.TYPE1
				is PDType1CFont -> PostScriptFontType.TYPE2
				is PDType3Font -> PostScriptFontType.TYPE3
				else -> PostScriptFontType.NONE
			}

			if (symbol != null && symbol != " ") {
				symbol.forEach {
					symbols.add(
						Symbol(it, Font(fontType, text.fontSize), Coordinate(text.xDirAdj to text.yDirAdj))
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
