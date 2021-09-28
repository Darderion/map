package com.github.darderion.mundaneassignmentpolice.utils

import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.*
import java.nio.file.StandardCopyOption


class FileUploadUtil {
	companion object {
		@Throws(IOException::class)
		fun saveFile(
			uploadDir: String?, fileName: String,
			multipartFile: MultipartFile
		) {
			val uploadPath: Path = Paths.get(uploadDir)
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath)
			}
			try {
				multipartFile.inputStream.use { inputStream ->
					val filePath: Path = uploadPath.resolve(fileName)
					Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)
				}
			} catch (ioe: IOException) {
				throw IOException("Could not save pdf file: $fileName", ioe)
			}
		}
	}
}
