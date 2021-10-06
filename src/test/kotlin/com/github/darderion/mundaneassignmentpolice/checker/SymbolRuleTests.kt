package com.github.darderion.mundaneassignmentpolice.checker

import com.github.darderion.mundaneassignmentpolice.checker.rule.SymbolRuleBuilder
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly

class SymbolRuleTests: StringSpec({
	"Symbol rule should detect incorrect symbols ? in links" {
		SymbolRuleBuilder()
			.ifSymbol('?')
			.ignoringAdjusting(*" ,0123456789".toCharArray())
			.shouldNotHaveNeighbor(*"[]".toCharArray())
			.getRule()
			.process(PDFBox().getPDF("src/test/cw1.pdf")).count() shouldBeExactly 4
	}
	"Symbol rule should detect incorrect usage of - symbol" {
		val shortdash = '-'

		SymbolRuleBuilder()
			.ifSymbol(shortdash)
			.shouldHaveNeighbor(*"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray())
			.shouldHaveNeighbor(*"абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".toCharArray())
			.getRule().process(PDFBox().getPDF("src/test/cw1.pdf")).count() shouldBeExactly 2
	}
	"Symbol rule should detect incorrect usage of -- symbol" {
		val mediumdash = '–'

		SymbolRuleBuilder()
			.ifSymbol(mediumdash)
			.shouldHaveNeighbor(*"0123456789".toCharArray())
			.getRule().process(PDFBox().getPDF("src/test/cw1.pdf")).count() shouldBeExactly 3
	}
	"Symbol rule should detect incorrect usage of --- symbol" {
		val longdash = '—'

		SymbolRuleBuilder()
			.ifSymbol(longdash)
			.shouldHaveNeighbor(' ')
			.getRule().process(PDFBox().getPDF("src/test/cw1.pdf")).count() shouldBeExactly 1

		SymbolRuleBuilder()
			.ifSymbol(longdash)
			.ignoringAdjusting(' ')
			.shouldNotHaveNeighbor(*"0123456789".toCharArray())
			.getRule().process(PDFBox().getPDF("src/test/cw1.pdf")).count() shouldBeExactly 2
	}
})
