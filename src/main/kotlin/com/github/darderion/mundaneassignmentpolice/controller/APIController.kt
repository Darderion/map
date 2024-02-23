package com.github.darderion.mundaneassignmentpolice.controller
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
import  com.github.darderion.mundaneassignmentpolice.rules.RULE_SET_RU
import com.github.darderion.mundaneassignmentpolice.statisticsservice.StatisticsService
import java.util.UUID
import java.io.File

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

const val pdfFolder = "uploads/"
const val developmentURL = "http://localhost:3000"
const val deploymentURL = "http://91.109.207.113"

const val url = developmentURL
data class Feedback(val pdfName: String, val comment: String, val title: String, val line: Int, val page: Int)
@RestController
@CrossOrigin(origins = [url])
class APIController {
	val pdfBox = PDFBox()
	val ruleSet = RULE_SET_RU

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
	@GetMapping("/api/viewPDFStatistic")
	fun getPDFStatistic(@RequestParam pdfName: String) =
		StatisticsService().getPDFStatistic("$pdfFolder$pdfName")
	@GetMapping("/api/viewRULE_SET")
	@ResponseBody
	fun viewRuleSet(): RuleSet {
		val ruleSet = RULE_SET_RU
		val uniqueRules = ruleSet.rules.distinctBy { it.name }
		return RuleSet(uniqueRules)
	}
	@PostMapping("/api/getPDFReview")
	fun getPDFReview(
		@RequestParam("pdf") multipartFile: MultipartFile,
		@RequestParam("locale") locale: String,
		@RequestParam("ruleSet") ruleSet2: List<String>
	): RedirectView? {
		val pdfReport = uploadPDF(multipartFile,ruleSet2)
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
	@GetMapping("/api/listUploadedPDFsWithReports")
	fun listUploadedPDFsWithReports(): List<Map<String, Any?>> {
		val folder = File(pdfFolder)
		val reports = mutableListOf<Map<String, Any?>>()
		val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
		val pdfFiles = folder.listFiles()?.filter { it.isFile&& it.name.endsWith(".pdf") }
		pdfFiles?.forEach { pdfFile ->
			val reportFile = File("$pdfFolder${File.separator}${pdfFile.name.plus("_report")}")

			val report = if (reportFile.exists()) {
				val jsonString = reportFile.readText()
				mapper.readValue(jsonString, DocumentReport::class.java)
			} else {
				null
			}
			val reportMap = mutableMapOf<String, Any?>()
			reportMap["name"] = pdfFile.name
			reportMap["documentReport"] = report
			reports.add(reportMap)
		}
		return reports
	}
	@GetMapping("/api/getReportByFileName")
	fun getReportByFileName(@RequestParam fileName: String): Map<String, Any?> {
		val folder = File(pdfFolder)
		val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
		val reportMap = mutableMapOf<String, Any?>()

		val pdfFile = File("$pdfFolder${File.separator}$fileName")
		val reportFile = File("$pdfFolder${File.separator}${fileName}_report")

		if (pdfFile.exists() && reportFile.exists()) {
			val jsonString = reportFile.readText()
			val report = mapper.readValue(jsonString, DocumentReport::class.java)

			reportMap["name"] = pdfFile.name
			reportMap["documentReport"] = report
		} else {
			reportMap["error"] = "ort not found"
		}

		return reportMap
	}

	@PostMapping("/api/uploadPDF")
	fun uploadPDF(@RequestParam("file") multipartFile: MultipartFile, @RequestParam("ruleSet") ruleSet2: List<String>): DocumentReport {
		if (multipartFile.originalFilename == null) {
			return DocumentReport.emptyFileName
		}
		val randomUUID = UUID.randomUUID().toString()
		val name = "$randomUUID"
		val pdfName = FileUploadUtil.saveFile(pdfFolder, multipartFile,multipartFile.originalFilename!!)
		val filteredRules = if (ruleSet2.isNotEmpty()) {
			RULE_SET_RU.rules.filter { ruleSet2.contains(it.name) }
		} else {
			RULE_SET_RU.rules
		}
		val newRuleSet = RuleSet(filteredRules.toMutableList())
		val ruleViolations = Checker().getRuleViolations("$pdfFolder$pdfName", newRuleSet)
		val docReport = DocumentReport(pdfName.plus("--$name _report"), ruleViolations, 0)
		val mapper = jacksonObjectMapper()
		val jsonString = mapper.writeValueAsString(docReport)
		File("$pdfFolder${File.separator}${pdfName.plus("--$name _report")}").writeText(jsonString)
		return docReport
	}
	@PostMapping("/api/submitFeedback")
	fun submitFeedback(@RequestParam("pdfName") pdfName: String,
					   @RequestParam("comment") comment: String,
					   @RequestParam("title") title: String,
					   @RequestParam("line") line: Int,
					   @RequestParam("page") page: Int): Feedback {
		val feedbackFolder = "feedback/"
		val feedbackDirectory = File(feedbackFolder)
		if (!feedbackDirectory.exists()) {
			feedbackDirectory.mkdirs() // Создать каталог если его нет
		}
		val feedback = Feedback(pdfName, comment, title, line, page)
		val mapper = jacksonObjectMapper()
		val jsonString = mapper.writeValueAsString(feedback)
		File("$feedbackFolder${File.separator}_${feedback.pdfName}_${feedback.line}_${feedback.page}.json").writeText(jsonString)
		return feedback;
	}
	private companion object {
		private val logger = KotlinLogging.logger {}
	}
}
