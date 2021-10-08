
export default class RuleViolation {
	line: Number;
	page: Number;
	message: String;

	constructor(line: Number, page: Number, message: String) {
		this.line = line
		this.page = page
		this.message = message
	}
}
