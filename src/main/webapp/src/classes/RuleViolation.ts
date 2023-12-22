import PDFLine from "./PDFLine";

export default class RuleViolation {
	lines: PDFLine[]
	message: string;
	type: string;

	constructor(lines: PDFLine[], message: string, type: string) {
		this.lines = lines
		this.message = message
		this.type = type
	}
}
