package com.github.darderion.mundaneassignmentpolice.pdfdocument

import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Coordinate
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Text
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox

class Annotations {
	companion object {
		private const val pageHeight = 842
		private const val pageWidth = 595.22

		fun underline(pdf: PDFDocument, lines: List<Text>) {
			var document = PDFBox().getDocument(pdf.name)
			lines.forEach { line ->
				document = PDFBox().addLine(document, line.page,
					Coordinate(line.position.x to (pageHeight - (line.position.y + 2))),
					(pageWidth - (line.position.x + 50)).toInt()
				)
			}
			document.save("build/ruleviolations/${
				pdf.name.split('/')[pdf.name.split('/').count() - 1].replace(".pdf", "")
			}${lines[0].line}-${lines[0].page}.pdf")
		}
	}
}
