package com.github.darderion.mundaneassignmentpolice.pdfdocument

import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Coordinate
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import java.nio.file.Files
import java.nio.file.Paths

class Annotations {
	companion object {
		private const val pageHeight = 842
		private const val pageWidth = 595.22

		fun underline(pdf: PDFDocument, lines: List<Line>): String {
			var document = PDFBox().getDocument(pdf.name)
			lines.forEach { line ->
				document = PDFBox().addLine(document, line.page,
					Coordinate(line.position.x to (pageHeight - (line.position.y + 2))),
					(pageWidth - (line.position.x + 50)).toInt()
				)
			}
			Files.createDirectories(Paths.get("build/ruleviolations/"))
			val fileName = "build/ruleviolations/${
				pdf.name.split('/')[pdf.name.split('/').count() - 1].replace(".pdf", "")
			}${lines[0].index}-${lines[0].page}.pdf"
			document.save(fileName)
			return fileName
		}
	}
}
