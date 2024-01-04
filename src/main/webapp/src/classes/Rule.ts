export default class Rule {
    area: string;
    name: string;
    type: string;
    description: string;

    constructor(area: string, name: string, type: string, description: string) {
        this.area = area;
        this.name = name;
        this.type = type;
        this.description = description;
    }
}
