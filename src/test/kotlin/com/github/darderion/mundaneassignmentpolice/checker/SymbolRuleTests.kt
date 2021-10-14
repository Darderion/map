package com.github.darderion.mundaneassignmentpolice.checker

import com.github.darderion.mundaneassignmentpolice.checker.rule.SymbolRuleBuilder
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.string.shouldInclude

class SymbolRuleTests: StringSpec({
	"Symbol rule should detect incorrect symbols ? in links" {
		SymbolRuleBuilder()
			.symbol('?')
			.ignoringAdjusting(*" ,0123456789".toCharArray())
			.shouldNotHaveNeighbor(*"[]".toCharArray())
			.getRule()
			.process(PDFBox().getPDF("src/test/cw1.pdf").also { it.text.forEach(::println) }).count() shouldBeExactly 4
	}
	"Symbol rule should detect incorrect usage of - symbol" {
		val shortDash = '-'

		SymbolRuleBuilder()
			.symbol(shortDash)
			.shouldHaveNeighbor(*"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray())
			.shouldHaveNeighbor(*"абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".toCharArray())
			.getRule().process(PDFBox().getPDF("src/test/cw1.pdf")).count() shouldBeExactly 2
	}
	"Symbol rule should detect incorrect usage of -- symbol" {
		val mediumDash = '–'

		SymbolRuleBuilder()
			.symbol(mediumDash)
			.shouldHaveNeighbor(*"0123456789".toCharArray())
			.getRule().process(PDFBox().getPDF("src/test/cw1.pdf")).count() shouldBeExactly 3
	}
	"Symbol rule should detect incorrect usage of --- symbol" {
		val longDash = '—'

		SymbolRuleBuilder()
			.symbol(longDash)
			.shouldHaveNeighbor(' ')
			.getRule().process(PDFBox().getPDF("src/test/cw1.pdf")).count() shouldBeExactly 1

		SymbolRuleBuilder()
			.symbol(longDash)
			.ignoringAdjusting(' ')
			.shouldNotHaveNeighbor(*"0123456789".toCharArray())
			.getRule().process(PDFBox().getPDF("src/test/cw1.pdf")).count() shouldBeExactly 2
	}
	"SomeText" {
		PDFBox().getPDF("src/test/cw1.pdf").toTextList().forEach(::println)

		"1" shouldInclude "2"
	}
})
