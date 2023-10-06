package com.github.darderion.mundaneassignmentpolice.controller

import com.github.darderion.mundaneassignmentpolice.checker.Checker
import com.github.darderion.mundaneassignmentpolice.checker.DocumentReport
import com.github.darderion.mundaneassignmentpolice.pdfdocument.Annotations
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Section
import com.github.darderion.mundaneassignmentpolice.rules.*
import com.github.darderion.mundaneassignmentpolice.utils.FileUploadUtil
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import mu.KotlinLogging
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.view.RedirectView
import java.io.File

const val pdfFolder = "uploads/"

const val developmentURL = "http://localhost:8080"
const val deploymentURL = "http://91.109.207.113"

const val url = developmentURL

@RestController
@CrossOrigin(origins = [url])
class APIController {
	val pdfBox = PDFBox()
	val ruleSet = RULE_SET_RU

	@GetMapping("/api/viewPDFText")
	fun getPDFText(@RequestParam pdfName: String) =
		pdfBox.getText("$pdfFolder$pdfName").also { logger.info("ViewPDF(pdfName = $pdfName)") }

	@GetMapping("/api/viewPDFSections")
	fun getPDFSections(@RequestParam pdfName: String): List<Section> {
		val pdf = pdfBox.getPDF("$pdfFolder$pdfName").also { logger.info("ViewPDFSections(pdfName = $pdfName)") }
		return if (pdf.areas != null) pdf.areas.sections else listOf()
	}

	@GetMapping("/api/viewPDFLines")
	fun getPDFLines(@RequestParam pdfName: String) =
		pdfBox.getLines("$pdfFolder$pdfName").also { logger.info("ViewPDFLines(pdfName = $pdfName)") }

	@GetMapping("/api/viewRuleViolations")
	fun getRulesViolations(@RequestParam pdfName: String) =
		Checker().getRuleViolations("$pdfFolder$pdfName", ruleSet).also { logger.info("ViewRuleViolations(pdfName = $pdfName)") }

	@GetMapping("/api/viewPDFImages")
	fun getPDFImages(@RequestParam pdfName: String) =
		pdfBox.getImages("$pdfFolder$pdfName").toList().also { logger.info("ViewPDFImages(pdfName = $pdfName)") }

	@PostMapping("/api/getPDFReview")
	fun getPDFReview(
		@RequestParam("pdf") multipartFile: MultipartFile,
		@RequestParam("locale") locale: String
	): RedirectView? {
		val pdfReport = uploadPDF(multipartFile)
		if (pdfReport.errorCode != 0) {
			return null
		}
		val fileName = pdfReport.name
		logger.info("UploadPDF(pdfName = $fileName)")
		return RedirectView("/#/viewPDF?pdfName=$fileName&numPages=${pdfBox.getPDFSize("$pdfFolder$fileName")}&locale=${locale}", true)
	}

	@GetMapping("/api/viewPDF.pdf")
	@ResponseBody
	fun getPDF(@RequestParam("pdfName") fileName: String) = File("$pdfFolder$fileName").readBytes()

	@GetMapping("/api/viewPDFRuleViolations.pdf")
	@ResponseBody
	fun getPDF(@RequestParam("pdfName") fileName: String,
			   @RequestParam("page") page: Int?,
			   @RequestParam("lines") lines: List<Int>?
	): ByteArray {
		val directory = "${pdfFolder}ruleviolations/"
		FileUploadUtil.removeRandomFile(directory, 1000)

		val pdf = PDFBox().getPDF("$pdfFolder$fileName")
		val pdf2 = Annotations.underline(pdf,
			if (page == null || lines == null) {
				Checker().getRuleViolations(fileName, ruleSet).map { it.lines }.flatten()
			} else
				pdf.text.filter { it.page == page && it.index >= lines.first() &&
						it.index <= lines.last() }
					.sortedBy { line -> line.index }
		)
		logger.info("File created: $pdf2")

		return File("${pdfFolder}ruleviolations/${fileName.replace(".pdf", "")}${(lines ?: listOf(-1)).first()}-${(lines ?: listOf(0)).last()}(${page}).pdf").readBytes()
	}

	@GetMapping("/api/getPDFSize")
	fun getPDFSize(@RequestParam pdfName: String) = pdfBox.getPDFSize("$pdfFolder$pdfName")

	@PostMapping("/api/uploadPDF")
	fun uploadPDF(@RequestParam("file") multipartFile: MultipartFile): DocumentReport {
		if (multipartFile.originalFilename == null) {
			return DocumentReport.emptyFileName
		}
		val pdfName = FileUploadUtil.saveFile(pdfFolder, multipartFile, folderMaxSize = 1000)
		val ruleViolations = Checker().getRuleViolations("$pdfFolder$pdfName", ruleSet)

		return DocumentReport(pdfName, ruleViolations, 0)
	}

	private companion object {
		private val logger = KotlinLogging.logger {}
	}
}
