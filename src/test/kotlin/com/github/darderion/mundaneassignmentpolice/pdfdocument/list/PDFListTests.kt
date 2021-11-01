package com.github.darderion.mundaneassignmentpolice.pdfdocument.list

import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import io.kotest.core.spec.style.StringSpec
import com.github.darderion.mundaneassignmentpolice.TestsConfiguration.Companion.resourceFolder

class PDFListTests: StringSpec({
	"getLists should return lists" {
		val document = PDFBox().getPDF(filePath)

		val lists = document.areas!!.lists
	}
}) {
	private companion object {
		const val filePath = "${resourceFolder}pdfdocument/list/PDFListTests.pdf"
	}
}
