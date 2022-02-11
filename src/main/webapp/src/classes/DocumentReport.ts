import RuleViolation from "./RuleViolation";

export default class DocumentReport {
	name: string;
	ruleViolations: RuleViolation[];
	errorCode: number;

	constructor(name: string, ruleViolations: RuleViolation[], errorCode: number) {
		this.name = name;
		this.ruleViolations = ruleViolations;
		this.errorCode = errorCode;
	}
}
