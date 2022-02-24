
import PDFLine from "./PDFLine";

export default class RuleViolation {
	lines: PDFLine[]
	message: string;

	constructor(lines: PDFLine[], message: string) {
		this.lines = lines
		this.message = message
	}
}
