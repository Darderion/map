package com.github.darderion.mundaneassignmentpolice.wrapper

import com.github.darderion.mundaneassignmentpolice.utils.imgToBase64String
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDResources
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.text.PDFTextStripper
import java.awt.image.RenderedImage
import java.io.File
import java.io.IOException

class PDFBox {
	val recentDocuments: HashMap<String, PDDocument> = hashMapOf()

	private fun getDocument(fileName: String): PDDocument {
		if (!recentDocuments.contains(fileName)) {
			recentDocuments[fileName] = PDDocument.load(File(fileName))
		}
		val document = recentDocuments[fileName]!!

		val accessPermission = document.currentAccessPermission
		if (!accessPermission.canExtractContent()) {
			throw IOException("You do not have permission to open '$fileName' file")
		}

		return document
	}

	/**
	 * Returns html representation of pdf's text content
	 * @param fileName pdf's filename
	 * @return html text content
	 */
	fun getText(fileName: String) = getPDF(fileName).toHTMLString()

	/**
	 * Returns PDFDocument object
	 * @param fileName pdf's filename
	 * @return PDFDocument
	 */
	fun getPDF(fileName: String): PDFDocument {
		val pdfLines: MutableList<PDFText> = mutableListOf()

		val document = getDocument(fileName)
		val stripper = PDFTextStripper()

		document.pages.forEachIndexed { index, pdPage ->
			stripper.startPage = index + 1
			stripper.endPage = stripper.startPage

			pdfLines.addAll(
				stripper.getText(document)
					.split("\n")
					.mapIndexed {
							lineIndex, lineText -> PDFText(lineIndex, index, lineText)
					}
			)
		}

		return PDFDocument(fileName, pdfLines)
	}

	/**
	 * Returns images from PDF in base64 string format
	 * @param fileName pdf's filename
	 * @return oredered set of images in base64 string format
	 */
	fun getImages(fileName: String): LinkedHashSet<String> {
		val document = getDocument(fileName)

		val images: MutableList<RenderedImage> = ArrayList()
		for (page in document.pages) {
			images.addAll(getImagesFromResources(page.resources))
		}
		return linkedSetOf(*images.map(::imgToBase64String).toTypedArray())
	}

	private fun getImagesFromResources(resources: PDResources): List<RenderedImage> {
		val images: MutableList<RenderedImage> = ArrayList()
		for (xObjectName in resources.xObjectNames) {
			val xObject = resources.getXObject(xObjectName)
			if (xObject is PDFormXObject) {
				images.addAll(getImagesFromResources(xObject.resources))
			} else if (xObject is PDImageXObject) {
				images.add(xObject.image)
			}
		}
		return images
	}
}
