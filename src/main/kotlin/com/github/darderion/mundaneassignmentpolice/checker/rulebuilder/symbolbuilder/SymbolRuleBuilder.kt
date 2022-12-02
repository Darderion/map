package com.github.darderion.mundaneassignmentpolice.checker.rulebuilder.symbolbuilder

import com.github.darderion.mundaneassignmentpolice.checker.Direction
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.BasicSymbolRule
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.SymbolRule
import com.github.darderion.mundaneassignmentpolice.checker.rulebuilder.NotRegionRuleBuilder
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import java.util.regex.Pattern

// Extension method
// https://stackoverflow.com/questions/5034442/indexes-of-all-occurrences-of-character-in-a-string
fun CharSequence.indicesOf(input: String): List<Int> =
	Regex(Pattern.quote(input))	// build regex
		.findAll(this)		// get the matches
		.map { it.range.first }	// get the index
		.toCollection(mutableListOf())	// collect the result as list

class SymbolRuleBuilder<out TBuilder: SymbolRuleBuilder<TBuilder>>(
	type: RuleViolationType = RuleViolationType.Error,
	name: String = "Rule name",
	region: PDFRegion = PDFRegion.EVERYWHERE
): NotRegionRuleBuilder<TBuilder>(type,name, region){

	private var symbol: Char = ' '
	private var ignoredNeighbors: MutableList<Char> = mutableListOf()
	private var notIgnoredNeighbors: MutableList<Char> = mutableListOf()
	private var ignoredIndexes: MutableList<Int> = mutableListOf()
	private var disallowedNeighbors: MutableList<Char> = mutableListOf()
	private var requiredNeighbors: MutableList<Char> = mutableListOf()
	private var direction: Direction = Direction.BIDIRECTIONAL
	private var neighborhoodSize: Int = 1

	infix fun symbol(symbol: Char) = this.also { this.symbol = symbol }

	fun ignoringAdjusting(vararg symbols: Char) = this.also { if (notIgnoredNeighbors.isEmpty()) ignoredNeighbors.addAll(symbols.toList())
															  else throw Exception("Up to one of the following methods can be used:" +
																				   " ignoringAdjusting() or ignoringEveryCharacterExcept().")}

	fun ignoringEveryCharacterExcept(vararg symbols: Char) = this.also { if (ignoredNeighbors.isEmpty()) notIgnoredNeighbors.addAll(symbols.toList())
																		 else throw Exception("Up to one of the following methods can be used:" +
																							  " ignoringAdjusting() or ignoringEveryCharacterExcept().")}

	fun ignoringIfIndex(index: Int) = this.also { ignoredIndexes.add(index) }

	fun shouldNotHaveNeighbor(vararg symbols: Char) = this.also { disallowedNeighbors.addAll(symbols.toList()) }

	fun shouldHaveNeighbor(vararg symbols: Char) = this.also { requiredNeighbors.addAll(symbols.toList()) }

	fun fromLeft() = this.also { direction = Direction.LEFT }

	fun fromRight() = this.also { direction = Direction.RIGHT }

	fun fromBothSides() = this.also { direction = Direction.BIDIRECTIONAL }

	infix fun from(direction: Direction) = this.also { this.direction = direction }

	fun inNeighborhood(size: Int) = this.also { this.neighborhoodSize = size }

	override fun getRule() = BasicSymbolRule(
		symbol,
		ignoredNeighbors,
		notIgnoredNeighbors,
		ignoredIndexes,
		disallowedNeighbors,
		requiredNeighbors,
		direction,
		neighborhoodSize,
		type,
		region,
		name
	) as SymbolRule
}
