
export default class PDFText {
	line: Number;
	page: Number;
	documentIndex: Number;
	content: String;

	constructor(line: Number, page: Number, documentIndex: Number, content: String) {
		this.line = line
		this.page = page
		this.documentIndex = documentIndex
		this.content = content
	}
}
