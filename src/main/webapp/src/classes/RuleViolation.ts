
import PDFText from "./PDFText";

export default class RuleViolation {
	lines: PDFText[]
	message: String;

	constructor(lines: PDFText[], message: String) {
		this.lines = lines
		this.message = message
	}
}
