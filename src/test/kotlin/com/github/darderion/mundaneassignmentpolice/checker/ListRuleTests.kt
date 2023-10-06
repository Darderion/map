package com.github.darderion.mundaneassignmentpolice.checker

import com.github.darderion.mundaneassignmentpolice.checker.rule.list.ListRuleBuilder
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea.TABLE_OF_CONTENT
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly
import com.github.darderion.mundaneassignmentpolice.TestsConfiguration.Companion.resourceFolder
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea.SECTION

class ListRuleTests: StringSpec({
	"List rule should detect lists of incorrect size in TABLE_OF_CONTENT" {
		ListRuleBuilder()
			.inArea(TABLE_OF_CONTENT)
			.disallowInSingleList {
				if (it.nodes.count() == 1) it.getText() else listOf()
			}.getRule()
			.process(PDFBox().getPDF(filePathTableOfContent)).count() shouldBeExactly 2
	}
	"List rule should detect lists of incorrect size in SECTION" {
		ListRuleBuilder()
			.inArea(SECTION)
			.disallowInSingleList {
				if (it.nodes.count() == 1) it.getText() else listOf()
			}.getRule()
			.process(PDFBox().getPDF(filePathSection)).count() shouldBeExactly 3
	}
}) {
	private companion object {
		const val filePathTableOfContent = "${resourceFolder}checker/ListRuleTestsTableOfContent.pdf"
		const val filePathSection = "${resourceFolder}checker/ListRuleTestsSection.pdf"
	}
}
