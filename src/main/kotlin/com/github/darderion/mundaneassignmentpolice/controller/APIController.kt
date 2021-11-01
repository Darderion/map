package com.github.darderion.mundaneassignmentpolice.controller

import com.github.darderion.mundaneassignmentpolice.checker.Checker
import com.github.darderion.mundaneassignmentpolice.utils.FileUploadUtil
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.view.RedirectView
import mu.KotlinLogging

const val pdfFolder = "build/"

@RestController
class APIController {
	val pdfBox = PDFBox()

	@GetMapping("/api/viewPDF")
	fun getPDFText(@RequestParam pdfName: String) =
		pdfBox.getText("$pdfFolder$pdfName").also { logger.info("ViewPDF(pdfName = $pdfName)") }

	@GetMapping("/api/viewPDFSections")
	fun getPDFSections(@RequestParam pdfName: String) =
		pdfBox.getPDF("$pdfFolder$pdfName").also { logger.info("ViewPDFSections(pdfName = $pdfName)") }
			.areas!!.sections

	@GetMapping("/api/viewPDFLines")
	fun getPDFLines(@RequestParam pdfName: String) =
		pdfBox.getLines("$pdfFolder$pdfName").also { logger.info("ViewPDFLines(pdfName = $pdfName)") }

	@GetMapping("/api/viewRuleViolations")
	fun getRulesViolations(@RequestParam pdfName: String) =
		Checker().getRuleViolations("$pdfFolder$pdfName").also { logger.info("ViewRuleViolations(pdfName = $pdfName)") }

	@GetMapping("/api/viewPDFImages")
	fun getPDFImages(@RequestParam pdfName: String) =
		pdfBox.getImages("$pdfFolder$pdfName").toList().also { logger.info("ViewPDFImages(pdfName = $pdfName)") }

	@PostMapping("/api/uploadPDF")
	fun uploadPDF(@RequestParam("pdf") multipartFile: MultipartFile): RedirectView? {
		if (multipartFile.originalFilename == null) {
			return null
		}
		val fileName = StringUtils.cleanPath(multipartFile.originalFilename!!)
		FileUploadUtil.saveFile(pdfFolder, fileName, multipartFile)
		logger.info("UploadPDF(pdfName = $fileName)")
		return RedirectView("/#/viewPDF?pdfName=$fileName", true)
	}

	private companion object {
		private val logger = KotlinLogging.logger {}
	}
}
