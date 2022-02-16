
export default class PDFLine {
	index: number;
	page: number;
	documentIndex: number;
	content: string;
	area: string;

	constructor(index: number, page: number, documentIndex: number, content: string, area: string) {
		this.index = index
		this.page = page
		this.documentIndex = documentIndex
		this.content = content
		this.area = area
	}
}
