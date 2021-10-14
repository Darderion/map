package com.github.darderion.mundaneassignmentpolice.wrapper

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.string.shouldInclude

class PDFBoxTests: StringSpec({
	"getText should return text from PDF" {
		val pdfBox = PDFBox()

		pdfBox.getText("src/test/pdf.pdf") shouldInclude "Lorem ipsum"

		// Stripper.getText != TextStripper.getText
		/*
		Maecenastinciduntestefciturligulaeuismod,sitametornareestvulputate.Row1Row2Row3Row4024681012Column1Column2Column3
		Maecenastinciduntestefficiturligulaeuismod,sitametornareestvulputate.
		 */
	}
	"getImages should return correct number of images" {
		val pdfBox = PDFBox()

		pdfBox.getImages("src/test/pdf.pdf").count() shouldBeExactly 1
	}
})
