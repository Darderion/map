
import PDFText from "./PDFText";

export default class RuleViolation {
	lines: PDFText[]
	message: string;

	constructor(lines: PDFText[], message: string) {
		this.lines = lines
		this.message = message
	}
}
