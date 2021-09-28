package com.github.darderion.mundaneassignmentpolice.wrapper

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File
import java.io.IOException

class PDFBox {
	companion object {
		fun getText(fileName: String): String {
			PDDocument.load(File("build/$fileName")).use { document ->
				val accessPermission = document.currentAccessPermission
				if (!accessPermission.canExtractContent()) {
					throw IOException("You do not have permission to extract text")
				}
				val stripper = PDFTextStripper()
				stripper.sortByPosition = true

				// Set the page interval to extract. If you don't, then all pages would be extracted.
				// stripper.startPage = 1
				// stripper.endPage = 100

				// let the magic happen
				val text = stripper.getText(document).trim { it <= ' ' }

				return text.replace("\n", "<br>")
			}
		}
	}
}
