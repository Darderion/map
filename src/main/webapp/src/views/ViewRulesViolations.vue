
<template>
	<div>
		<div v-show="!this.$store.getters.getContainsErrors" class="messageDiv">
			Ошибок не найдено!
		</div>
		<div v-show="this.$store.getters.getContainsErrors">
			<div v-show="this.$store.getters.getContainsAreaErrors" class="messageDivText">
				<div style="color: #A22; font-size: 32px;">Приложение не обработало PDF</div>
				<br>
				<br>Возможные причины и способы их проверки:
				<br><div style="width: 50%; margin: auto;"><ul>
					<li>Зайдите в раздел <b><i>View as Text</i></b>. Если там текст отображается как "Ñîäåðæàíèå Ïîñòàíîâêà öåëè è çàäà÷", то проблема со шрифтами.<br><br></li>
					<li>Зайдите в подраздел <b><i>View as Text</i></b> -> <b><i>Highlight sections</i></b>. Если разделы не отображаются разными цветами, то возможна проблема с оглавлением.</li>
					</ul>
					</div>
				<br>
			</div>
			<div v-show="!this.$store.getters.getContainsAreaErrors">
				<div class="messageDiv">Выберите ошибку из списка ошибок для отображения соответствующей страницы.</div>
				<ul id="rulesViolations">
					<li v-for="ruleViolation in this.$store.getters.getRuleViolations"
					:key="'rulesViolations'+ruleViolation.toString()">{{ruleViolation.message}} > Line {{ruleViolation.lines[0].index}}, page {{ruleViolation.lines[0].page}}</li>
				</ul>
				<div id="ruleViolation" v-show="curPage != -1">
					<pdf
						:src="`api/viewPDFRuleViolations.pdf?pdfName=${this.$store.getters.getPdfName}&page=${curPage}&line=${curLine}`"
						:page="curPage + 1"
						style="display: inline-block; width: 100%"
					></pdf>
				</div>
			</div>
		</div>
	</div>
</template>

<script lang="ts">

import { Component, Prop, Vue } from 'vue-property-decorator';

import PDFTextComponent from '../components/PDFTextComponent.vue'
import pdf from 'vue-pdf'
import RuleViolation from '@/classes/RuleViolation';

@Component({
	components: {
		PDFTextComponent,
		pdf
	},
})

export default class ViewRulesViolations extends Vue {
	listElement = (document.getElementById('rulesViolations') as HTMLUListElement) ?
		document.getElementById('rulesViolations') as HTMLUListElement: undefined

	curPage = -1
	curLine = 0
	
	mounted() {
		if (this.listElement == undefined) {
			this.listElement = document.getElementById('rulesViolations') as HTMLUListElement
		}
		this.listElement.onclick = (event: MouseEvent) => {
			// eslint-disable-next-line @typescript-eslint/ban-ts-comment
			// @ts-ignore: Object is possibly 'null'.
			const element = (event.target as HTMLElement)
			if (element.tagName != "LI") return;
			
			this.singleSelect(element);
		}

		// prevent unneeded selection of list elements on clicks
		this.listElement.onmousedown = function() {
			return false;
		};
	}

	singleSelect(li: HTMLElement) {
		// eslint-disable-next-line @typescript-eslint/ban-ts-comment
		// @ts-ignore: Object is possibly 'null'.
		let selected = this.listElement.querySelectorAll('.selected');
		for(let elem of selected) {
			elem.classList.remove('selected');
		}
		li.classList.add('selected');

		// eslint-disable-next-line @typescript-eslint/ban-ts-comment
		// @ts-ignore: Object is possibly 'null'.
		const page = Number(li.textContent.split(' ')[li.textContent.split(' ').length - 1])

		// eslint-disable-next-line @typescript-eslint/ban-ts-comment
		// @ts-ignore: Object is possibly 'null'.
		const lineText = li.textContent.split('Line ')[1].split(', page')[0]

		this.curPage = page
		this.curLine = Number(lineText)
	}

/*
	updated() {
		this.noErrorsFound = this.$store.getters.getRuleViolations().count() == 0
		this.noAreaErrorsFound =
			this.$store.getters.getRuleViolations().count() == 0 ||
			this.$store.getters.getRuleViolations().filter((ruleViolation: RuleViolation) => ruleViolation.message == 'PDFArea').count() == 0
		console.log(this.$store.getters.getRuleViolations())
		console.log(this.$store.getters.getRuleViolations().count() == 0)
		console.log(this.$store.getters.getRuleViolations().filter((ruleViolation: RuleViolation) => ruleViolation.message == 'PDFArea').count() == 0)
	}
*/
}
</script>

<style scoped>
#ruleViolation {
	float: right;
	width: 75%;
}

#rulesViolations {
	float: left;
	width: 20%;
}

.selected {
	background: #e74c3c;
}

#rulesViolations li {
	cursor: pointer;
}

.messageDiv {
	font-size: 32px;
}

.messageDivText {
	font-size: 24px;
}
</style>
