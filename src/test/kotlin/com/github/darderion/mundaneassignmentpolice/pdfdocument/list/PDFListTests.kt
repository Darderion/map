package com.github.darderion.mundaneassignmentpolice.pdfdocument.list

import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import io.kotest.core.spec.style.StringSpec
import com.github.darderion.mundaneassignmentpolice.TestsConfiguration.Companion.resourceFolder
import io.kotest.matchers.ints.shouldBeExactly

class PDFListTests: StringSpec({
	"getLists should return lists" {
		val document = PDFBox().getPDF(filePath)

		val lists = document.areas!!.lists
	}
	"getLists should return correct number of lists" {
		val document = PDFBox().getPDF(filePath)

		val lists = document.areas!!.lists
		lists.count() shouldBeExactly 8
	}
	"getLists should return correct lists" {
		val document = PDFBox().getPDF(filePath)

		val lists = document.areas!!.lists

		val pdfList = PDFList(
			nodes = mutableListOf(
				PDFList(
					"Item1", mutableListOf(
						PDFList("Item11"),
						PDFList("Item12")
					)
				),
				PDFList("Item2"),
				PDFList("Item3")
			)
		)

		lists.count { list ->
			list.map {
				it.text.joinToString("\n")
			}.toString() == pdfList.toString()
		} shouldBeExactly 1
	}
}) {
	private companion object {
		const val filePath = "${resourceFolder}pdfdocument/list/PDFListTests.pdf"
	}
}
