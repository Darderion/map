package com.github.darderion.mundaneassignmentpolice.checker.rulebuilder.wordbuilder

import com.github.darderion.mundaneassignmentpolice.checker.Direction
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.word.BasicWordRule
import com.github.darderion.mundaneassignmentpolice.checker.rule.word.WordRule
import com.github.darderion.mundaneassignmentpolice.checker.rulebuilder.NotRegionRuleBuilder
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion

fun splitToWordsAndPunctuations(str: String): List<String> {
	val wordsAndPunctuations: MutableList<String> = mutableListOf("")
	var index = 0
	for (symbol in str) {
		if (symbol.isLetterOrDigit()) {
			wordsAndPunctuations[index] += symbol.toString()
			continue
		}
		wordsAndPunctuations.addAll(listOf(symbol.toString(), ""))
		index += 2
	}

	return wordsAndPunctuations.filter { it != "" }
}

class WordRuleBuilder<out TBuilder: WordRuleBuilder<TBuilder>>(
	type: RuleViolationType = RuleViolationType.Error,
	name: String = "Rule name",
	region: PDFRegion = PDFRegion.EVERYWHERE
) : NotRegionRuleBuilder<TBuilder>(type,name, region){
	private var word: String = " "
	private var ignoredNeighbors: MutableList<Regex> = mutableListOf()
	private var notIgnoredNeighbors: MutableList<Regex> = mutableListOf()
	private var ignoredIndexes: MutableList<Int> = mutableListOf()
	private var isPunctuationIgnored: Boolean = false
	private var areWordsIgnored: Boolean = false
	private var disallowedNeighbors: MutableList<Regex> = mutableListOf()
	private var requiredNeighbors: MutableList<Regex> = mutableListOf()
	private var direction: Direction = Direction.BIDIRECTIONAL
	private var neighborhoodSize: Int = 1
	private var numberOfNeighbors: Int = 1

	infix fun word(word: String) = this.also { this.word = word }

	fun ignoringAdjusting(vararg words: Regex) = this.also {
		if (notIgnoredNeighbors.isEmpty()) ignoredNeighbors.addAll(words.toList())
		else throw Exception(
			"Up to one of the following methods can be used:" +
					" ignoringAdjusting() or ignoringEveryCharacterExcept()."
		)
	}

	fun ignoringEveryWordExcept(vararg words: Regex) = this.also {
		if (ignoredNeighbors.isEmpty()) notIgnoredNeighbors.addAll(words.toList())
		else throw Exception(
			"Up to one of the following methods can be used:" +
					" ignoringAdjusting() or ignoringEveryCharacterExcept()."
		)
	}

	fun ignoringIfIndex(index: Int) = this.also { ignoredIndexes.add(index) }

	fun ignoringPunctuation(ignore: Boolean) = this.also {
		if (!ignore || !areWordsIgnored) isPunctuationIgnored = ignore
		else throw Exception(
			"Up to one of the following methods can be used:" +
					" ignoringPunctuation() or ignoringWords()."
		)
	}

	fun ignoringWords(ignore: Boolean) = this.also {
		if (!ignore || !isPunctuationIgnored) areWordsIgnored = ignore
		else throw Exception(
			"Up to one of the following methods can be used:" +
					" ignoringPunctuation() or ignoringWords()."
		)
	}

	fun shouldNotHaveNeighbor(vararg words: Regex) =
		this.also { disallowedNeighbors.addAll(words.toList()) }

	fun shouldHaveNeighbor(vararg words: Regex) =
		this.also { requiredNeighbors.addAll(words.toList()) }

	fun fromLeft() = this.also { direction = Direction.LEFT }

	fun fromRight() = this.also { direction = Direction.RIGHT }

	fun fromBothSides() = this.also { direction = Direction.BIDIRECTIONAL }

	fun inNeighborhood(size: Int) = this.also { this.neighborhoodSize = size }

	fun shouldHaveNumberOfNeighbors(number: Int) = this.also { this.numberOfNeighbors = number }

	override fun getRule() = BasicWordRule(
		word,
		ignoredNeighbors,
		notIgnoredNeighbors,
		ignoredIndexes,
		isPunctuationIgnored,
		areWordsIgnored,
		disallowedNeighbors,
		requiredNeighbors,
		direction,
		neighborhoodSize,
		numberOfNeighbors,
		type,
		region,
		name
	) as WordRule
}