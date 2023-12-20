package com.github.darderion.mundaneassignmentpolice.codeDetector

import com.github.darderion.mundaneassignmentpolice.utils.codeDetector.CodeDetector
import com.github.darderion.mundaneassignmentpolice.utils.codeDetector.Parser
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
    "Multi-character symbols must be parsed correctly" {
        (Parser.parseString("// /* /** = == === !=must be recognized::+=-=  *=->") == listOf(
            "//", "/*", "/**", "=", "==", "===", "!=", "must", "be", "recognized", "::", "+=", "-=", "*=", "->"
        )) shouldBe true
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
    "test case from database 5" {
        CodeDetector.isLikelyCode("4") shouldBe false
    }
    "test case from A.Bochkarev" {
        CodeDetector.isLikelyCode("str.equals(\"greater\" || testField != sum) {") shouldBe true
    }

    // 0.5 is the highest border that can be reached in particular cases, not in average
    "FREQUENCY_THRESHOLD is counted correctly" {
        (Threshold.FREQUENCY_THRESHOLD <= 0.95) shouldBe true
    }
    "PROPERTIES_THRESHOLD is counted correctly" {
        (Threshold.PROPERTIES_THRESHOLD <= 0.5) shouldBe true
    }
})

