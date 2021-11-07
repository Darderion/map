package com.github.darderion.mundaneassignmentpolice.wrapper

import com.github.darderion.mundaneassignmentpolice.TestsConfiguration.Companion.resourceFolder
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Coordinate
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldInclude
import org.apache.pdfbox.pdmodel.PDDocument
import java.io.File

class PDFBoxTests: StringSpec({
	"getText should return text from PDF" {
		pdfBox.getText(filePathPDF) shouldInclude "Lorem ipsum"
	}
	"getImages should return correct number of images" {
		pdfBox.getImages(filePathPDF).count() shouldBeExactly 1

		pdfBox.getImages(filePathUTF).count() shouldBeExactly 0
	}
	"getText should process surrogate pair symbols" {
		pdfBox.getText(filePathUTF)
	}
	"getText should process ligature symbols" {
		pdfBox.getText(filePathPDF) shouldContain "ffi"
	}
}) {
	override fun beforeEach(testCase: TestCase) {
		super.beforeEach(testCase)
		pdfBox = PDFBox()
	}

	private companion object {
		const val filePathPDF = "${resourceFolder}wrapper/PDFBoxTestsCW.pdf"
		const val filePathUTF = "${resourceFolder}wrapper/PDFBoxTestsUTF.pdf"

		var pdfBox = PDFBox()
	}
}
