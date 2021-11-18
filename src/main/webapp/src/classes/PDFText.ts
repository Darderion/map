
export default class PDFText {
	line: number;
	page: number;
	documentIndex: number;
	content: string;
	area: string;

	constructor(line: number, page: number, documentIndex: number, content: string, area: string) {
		this.line = line
		this.page = page
		this.documentIndex = documentIndex
		this.content = content
		this.area = area
	}
}
