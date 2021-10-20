package com.github.darderion.mundaneassignmentpolice.pdfdocument

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldInclude
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea.*
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe

class PDFDocumentTests: StringSpec({
	"PDFDocument should contain TITLE_PAGE's lines" {
		PDFDocument("pdf.pdf", lines).text.any { it.area == TITLE_PAGE } shouldBe true
	}
	"PDFDocument should contain PAGE_INDEX's lines" {
		PDFDocument("pdf.pdf", lines).text.any { it.area == PAGE_INDEX } shouldBe true
	}
	"PDFDocument should contain TABLE_OF_CONTENT's lines" {
		PDFDocument("pdf.pdf", lines).text.any { it.area == TABLE_OF_CONTENT } shouldBe true
	}
	"PDFDocument should contain SECTION's lines" {
		PDFDocument("pdf.pdf", lines).text.any { it.area == SECTION } shouldBe true
	}
	"PDFDocument should contain FOOTNOTE's lines" {
		PDFDocument("pdf.pdf", lines).text.any { it.area == FOOTNOTE } shouldBe true
	}
	"PDFDocument should contain BIBLIOGRAPHY's lines" {
		PDFDocument("pdf.pdf", lines).text.any { it.area == BIBLIOGRAPHY } shouldBe true
	}
}) {
	private companion object {
		private val lines = PDFBox().getLines("src/test/cw1.pdf")
	}
}
