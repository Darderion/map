package com.github.darderion.mundaneassignmentpolice.controller

import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class APIController {
	@GetMapping("/api/viewPDF")
	fun getPDFText(@RequestParam pdfName: String): String {
		return PDFBox.getText(pdfName)
	}
}
