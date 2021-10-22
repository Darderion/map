package com.github.darderion.mundaneassignmentpolice.wrapper

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldInclude

class PDFBoxTests: StringSpec({
	"getText should return text from PDF" {
		pdfBox.getText("src/test/pdf.pdf") shouldInclude "Lorem ipsum"
	}
	"getImages should return correct number of images" {
		pdfBox.getImages("src/test/pdf.pdf").count() shouldBeExactly 1
	}
	"getText should process surrogate pair symbols" {
		pdfBox.getText("src/test/UTF-16.pdf")
	}
	"getText should process ligature symbols" {
		pdfBox.getText("src/test/cw1.pdf") shouldContain "ffi"
	}
}) {
	override fun beforeEach(testCase: TestCase) {
		super.beforeEach(testCase)
		pdfBox = PDFBox()
	}

	private companion object {
		var pdfBox = PDFBox()
	}
}
