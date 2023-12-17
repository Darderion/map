package com.github.darderion.mundaneassignmentpolice.codeDetector

import com.github.darderion.mundaneassignmentpolice.utils.codeDetector.CodeDetector
import com.github.darderion.mundaneassignmentpolice.utils.codeDetector.Threshold
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class CodeDetectorTests : StringSpec({
    "Code patterns must be recognized" {
        CodeDetector.isLikelyCode("int main() {return 0;}") shouldBe true
    }
    "Code patterns must not be recognized" {
        CodeDetector.isLikelyCode("This will return false (though we need to test); test.") shouldBe false
    }
    "test case from database 1" {
        CodeDetector.isLikelyCode("laborum.") shouldBe false
    }
    "test case from database 2" {
        CodeDetector.isLikelyCode("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod") shouldBe false
    }
    "test case from database 3" {
        CodeDetector.isLikelyCode("List.count == 2:") shouldBe true
    }
    "test case from database 4" {
        CodeDetector.isLikelyCode("(a) Item1") shouldBe false
    }
    "FREQUENCY_THRESHOLD is counted correctly" { // 0.5 is the highest average that can be reached in particular cases, not in average
        (Threshold.FREQUENCY_THRESHOLD <= 0.5) shouldBe true
    }
    "PROPERTIES_THRESHOLD is counted correctly" {
        TODO()
    }
})

