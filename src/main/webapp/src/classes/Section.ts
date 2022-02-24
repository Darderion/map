
/**
 * 1	|	section1text1
 * 2	|	section1text2
 * 3	|	SECTION2 TITLE
 * 4	|		IS A 2 LINE TITLE
 * 5	|	section2text1
 * 6	|	section2text2
 * 7	|	section2text3
 * 
 * section2.titleIndex = 3
 * section2.sectionIndex = 5
 */
export default class Section {
	title: string;
	titleIndex: number;
	contentIndex: number;

	constructor(title: string, titleIndex: number, contentIndex: number) {
		this.title = title
		this.titleIndex = titleIndex
		this.contentIndex = contentIndex
	}
}
