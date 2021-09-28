package com.github.darderion.mundaneassignmentpolice.controller

import com.github.darderion.mundaneassignmentpolice.utils.FileUploadUtil
import org.springframework.stereotype.Controller
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.view.RedirectView
import java.io.IOException


@Controller
class RouterController {

	@GetMapping("/")
	fun home() = "forward:/app/index.html"

	@PostMapping("/pdf/upload")
	fun uploadPDF(
		@RequestParam("pdf") multipartFile: MultipartFile
	): RedirectView? {
		if (multipartFile.originalFilename == null) {
			return null
		}
		val fileName: String = StringUtils.cleanPath(multipartFile.originalFilename!!)
		FileUploadUtil.saveFile("build", fileName, multipartFile)
		return RedirectView("/", true)
	}
}
