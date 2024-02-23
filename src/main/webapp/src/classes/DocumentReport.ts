import RuleViolation from "./RuleViolation";

/**
 * Document report contains PDF's name, list of rule violations and error code
 * Error codes:
 * 		0	--	No Errors
 * 		-1	--	Empty File Name
 */
export default class DocumentReport {
	name: string;
	fileName: string;
	ruleViolations: RuleViolation[];
	errorCode: number;

	constructor(name: string, fileName: string, ruleViolations: RuleViolation[], errorCode: number) {
		this.name = name;
		this.fileName = fileName;
		this.ruleViolations = ruleViolations;
		this.errorCode = errorCode;
	}
}
