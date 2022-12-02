package com.github.darderion.mundaneassignmentpolice.rules

import com.github.darderion.mundaneassignmentpolice.checker.rule.word.WordRule
import com.github.darderion.mundaneassignmentpolice.checker.rule.word.or
import com.github.darderion.mundaneassignmentpolice.checker.rulebuilder.wordbuilder.WordRuleBuilder
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion


val smallNumbersRuleName = "Неправильное написание целых чисел от 1 до 9"
val smallNumbersRuleArea =
        PDFRegion.EVERYWHERE.except(PDFArea.PAGE_INDEX, PDFArea.TABLE_OF_CONTENT, PDFArea.BIBLIOGRAPHY)
val allowedWordsOnLeft = arrayOf(
        Regex("""[Рр]ис[a-я]*"""),
        Regex("""[Тт]абл[a-я]*"""), Regex("""[Сс]х[a-я]*"""),
        Regex("""[Dd]ef[a-z]*"""), Regex("""[Оо]пр[а-я]*"""),
        Regex("""[Tt]h[a-z]*"""), Regex("""[Тт]еорема""")
)
val allowedWordsOnRight = arrayOf(
        Regex("""[Gg][Bb]"""), Regex("""[Гг][Бб]"""),
        Regex("""[Mm][Bb]"""), Regex("""[Мм][Бб]"""),
        Regex("""[Gg][Hh][Zz]"""), Regex("""[Гг][Цц]"""),
        Regex("""→""")
)

val smallNumbersRuleBuilder1 = WordRuleBuilder<WordRuleBuilder<*>>()		//for nearest words
        .called(smallNumbersRuleName)
        .inArea(smallNumbersRuleArea)
        .ignoringAdjusting(Regex("""\s"""), Regex("""\."""))
        .ignoringIfIndex(0)

val smallNumbersRuleBuilder2 = WordRuleBuilder<WordRuleBuilder<*>>()			//for decimal fractions and version numbers
        .called(smallNumbersRuleName)
        .inArea(smallNumbersRuleArea)
        .shouldHaveNeighbor(Regex("""\."""), Regex(""","""),
                Regex("""[0-9]+"""))
        .shouldHaveNumberOfNeighbors(2)

val smallNumbersRuleBuilder3 = WordRuleBuilder<WordRuleBuilder<*>>()			//for links
        .called(smallNumbersRuleName)
        .inArea(smallNumbersRuleArea)
        .fromLeft()
        .ignoringWords(true)
        .ignoringAdjusting(Regex(""","""), Regex("""\s"""))
        .shouldHaveNeighbor(Regex("""\["""))

val RULES_SMALL_NUMBERS = List<WordRule>(9) { index ->
    smallNumbersRuleBuilder1.word((index + 1).toString())
            .fromLeft().shouldHaveNeighbor(*allowedWordsOnLeft).getRule() or
            smallNumbersRuleBuilder1.word((index + 1).toString())
                    .fromRight().shouldHaveNeighbor(*allowedWordsOnRight).getRule() or
            smallNumbersRuleBuilder2.word((index + 1).toString()).fromLeft().getRule() or
            smallNumbersRuleBuilder2.fromRight().getRule() or
            smallNumbersRuleBuilder3.word((index + 1).toString()).getRule()
}
