package com.github.darderion.mundaneassignmentpolice.wrapper

import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Coordinate
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Font
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Symbol
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.TextPosition
import java.text.Normalizer

class PDFStripper: PDFTextStripper() {
	val symbols: MutableList<Symbol> = mutableListOf()

	/**
	 * Override the default functionality of PDFTextStripper.writeString()
	 */
	override fun writeString(string: String?, textPositions: List<TextPosition>) {
		var stringIndex = 0
		for (textPosition in textPositions) {
			val text = textPosition.unicode
			val normalizedText = if (text == string!!.substring(stringIndex until (stringIndex + text.length)))
				text
			else
				Normalizer.normalize(text, Normalizer.Form.NFKC) // same normalization form as used in PDFTextStripper

			if (normalizedText != null && normalizedText != " ") {
				normalizedText.forEach {
					symbols.add(
						Symbol(it, Font(textPosition.fontSize), Coordinate(textPosition.xDirAdj to textPosition.yDirAdj))
					)
				}
			}
			stringIndex += normalizedText.length
			/*
			words.add(text.unicode + " [(X=" + text.xDirAdj + ",Y=" +
						text.yDirAdj + ") height=" + text.heightDir + " width=" +
						text.widthDirAdj + "]")
			 */
		}
	}
}
