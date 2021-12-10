package com.github.darderion.mundaneassignmentpolice.utils

import com.github.darderion.mundaneassignmentpolice.controller.pdfFolder
import org.springframework.web.multipart.MultipartFile
import java.awt.image.RenderedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.UncheckedIOException
import java.nio.file.*
import java.util.*
import javax.imageio.ImageIO


class FileUploadUtil {
	companion object {
		fun removeRandomFile(directory: String, filesInFolderRequired: Int) {
			val pdfFolder = File(directory)
			if (!pdfFolder.exists()) {
				pdfFolder.mkdirs()
			}
			val files = File(directory).listFiles().toList().filter { it.isFile }
			if (files.count() >= filesInFolderRequired) {
				files.shuffled().first().delete()
			}
		}

		fun saveFile(
			uploadDir: String,
			fileName: String,
			multipartFile: MultipartFile
		) {
			val uploadPath = Paths.get(uploadDir)
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath)
			}
			try {
				multipartFile.inputStream.use { inputStream ->
					val filePath = uploadPath.resolve(fileName)
					Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)
				}
			} catch (ioe: IOException) {
				throw IOException("Could not save pdf file: $fileName", ioe)
			}
		}
	}
}
