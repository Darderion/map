
export default class DocumentReport {
	name: string;
	numberOfViolations: number;
	errorCode: number;

	constructor(name: string, numberOfViolations: number, errorCode: number) {
		this.name = name;
		this.numberOfViolations = numberOfViolations;
		this.errorCode = errorCode;
	}
}
