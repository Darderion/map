package com.github.darderion.mundaneassignmentpolice.pdfdocument.list

import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import io.kotest.core.spec.style.StringSpec

class PDFListTests: StringSpec({
	"getLists should return lists" {
		val document = PDFBox().getPDF("src/test/lists.pdf")
	}
})
