package com.github.darderion.mundaneassignmentpolice.utils

import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.nio.file.*
import java.security.MessageDigest
import java.util.*
import kotlin.math.abs
import kotlin.random.Random


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

		fun getFileNameNextInt() = (abs(Random.nextInt() % 10000)).toString() + (abs(Random.nextInt() % 10000)).toString()

		fun getFileName(file: MultipartFile): String {
			val uploadBytes: ByteArray = file.bytes
			val md5: MessageDigest = MessageDigest.getInstance("MD5")
			val digest: ByteArray = md5.digest(uploadBytes)
			val hashString = BigInteger(1, digest).toString(16)
			println("File hash: $hashString")

			return hashString
		}

		fun saveFile(
			uploadDir: String,
			multipartFile: MultipartFile,
			fileName: String = getFileName(multipartFile),
			folderMaxSize: Int? = null
		): String {
			if (folderMaxSize != null) {
				removeRandomFile(uploadDir, folderMaxSize)
			}
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
			return fileName
		}
	}
}
