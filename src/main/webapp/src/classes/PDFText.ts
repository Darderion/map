
export default class PDFText {
	line: Number;
	page: Number;
	documentIndex: Number;
	content: String;
	area: String;

	constructor(line: Number, page: Number, documentIndex: Number, content: String, area: String) {
		this.line = line
		this.page = page
		this.documentIndex = documentIndex
		this.content = content
		this.area = area
	}
}
