package com.github.darderion.mundaneassignmentpolice.checker.rule.symbol

import com.github.darderion.mundaneassignmentpolice.checker.Direction
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion.Companion.EVERYWHERE
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion.Companion.NOWHERE
import java.util.regex.Pattern

// Extension method
// https://stackoverflow.com/questions/5034442/indexes-of-all-occurrences-of-character-in-a-string
fun CharSequence.indicesOf(input: String): List<Int> =
	Regex(Pattern.quote(input))	// build regex
		.findAll(this)		// get the matches
		.map { it.range.first }	// get the index
		.toCollection(mutableListOf())	// collect the result as list

class SymbolRuleBuilder {
	private var symbol: Char = ' '
	private var ignoredNeighbors: MutableList<Char> = mutableListOf()
	private var ignoredIndexes: MutableList<Int> = mutableListOf()
	private var disallowedNeighbors: MutableList<Char> = mutableListOf()
	private var requiredNeighbors: MutableList<Char> = mutableListOf()
	private var direction: Direction = Direction.BIDIRECTIONAL
	private var neighborhoodSize: Int = 1
	private var name: String = "Rule name"
	private var region: PDFRegion = EVERYWHERE

	fun symbol(symbol: Char) = this.also { this.symbol = symbol }

	fun called(name: String) = this.also { this.name = name }

	fun ignoringAdjusting(vararg symbols: Char) = this.also { ignoredNeighbors.addAll(symbols.toList()) }

	fun ignoringIfIndex(index: Int) = this.also { ignoredIndexes.add(index) }

	fun shouldNotHaveNeighbor(vararg symbols: Char) = this.also { disallowedNeighbors.addAll(symbols.toList()) }

	fun shouldHaveNeighbor(vararg symbols: Char) = this.also { requiredNeighbors.addAll(symbols.toList()) }

	fun fromLeft() = this.also { direction = Direction.LEFT }

	fun fromRight() = this.also { direction = Direction.RIGHT }

	fun fromBothSides() = this.also { direction = Direction.BIDIRECTIONAL }

	infix fun inArea(area: PDFArea) = this.also { region = NOWHERE.except(area) }

	infix fun inArea(region: PDFRegion) = this.also { this.region = region }


	fun getRule() = BasicSymbolRule(
		symbol,
		ignoredNeighbors,
		ignoredIndexes,
		disallowedNeighbors,
		requiredNeighbors,
		direction,
		neighborhoodSize,
		region,
		name
	) as SymbolRule
}
