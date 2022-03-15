
<template>
	<div>
		<div v-if="this.$store.getters.pdfName">
			<div v-show="!this.$store.getters.getContainsErrors" class="messageDiv">
				{{ $t('page.rulesViolations.noErrors')}}
			</div>
			<div v-show="this.$store.getters.getContainsErrors">
				<div v-show="this.$store.getters.getContainsAreaErrors" class="messageDivText">
					<div style="color: #A22; font-size: 32px;">{{ $t('page.rulesViolations.errors')}}</div>
					<br>
					<br>{{ $t('page.rulesViolations.suggestions')}}
					<br><div style="width: 50%; margin: auto;"><ul>
						<li>{{ $t('page.rulesViolations.suggestion_1_1')}} <b><i>{{ $t('page.viewText.title')}}</i></b>. {{ $t('page.rulesViolations.suggestion_1_2')}} "Ñîäåðæàíèå Ïîñòàíîâêà öåëè è çàäà÷" {{ $t('page.rulesViolations.suggestion_1_3')}}<br><br></li>
						<li>{{ $t('page.rulesViolations.suggestion_2_1')}} <b><i>{{ $t('page.viewText.title')}}</i></b> -> <b><i>{{ $t('page.viewText.highlightSections')}}</i></b>. {{ $t('page.rulesViolations.suggestion_2_2')}}</li>
						</ul>
						</div>
					<br>
				</div>
				<div v-show="!this.$store.getters.getContainsAreaErrors">
					<div class="messageDiv">{{ $t('page.rulesViolations.selectRuleViolation')}}</div>
					<ul id="rulesViolations">
						<li v-for="ruleViolation in this.$store.getters.getRuleViolations"
						:key="'rulesViolations'+ruleViolation.toString()">{{ruleViolation.message}} >
              {{(ruleViolation.lines.length > 1) ? $t('page.rulesViolations.lines') : $t('page.rulesViolations.line')}}
              {{(ruleViolation.lines.length > 1) ?
                ruleViolation.lines.map((line) => line.index)[0] + "-" + ruleViolation.lines.map((line) => line.index)[ruleViolation.lines.length - 1] :
                ruleViolation.lines[0].index}},
              {{ $t('page.rulesViolations.page')}} {{ruleViolation.lines[0].page}}</li>
					</ul>
					<div id="ruleViolation" v-show="curPage != -1">
						<pdf
							:src="`api/viewPDFRuleViolations.pdf?pdfName=${this.$store.getters.getPdfName}&page=${curPage}&lines=${curLines}`"
							:page="curPage + 1"
							style="display: inline-block; width: 100%"
						></pdf>
					</div>
				</div>
			</div>
		</div>
		<div v-else>
			<NoPDFComponent/>
		</div>
	</div>
</template>

<script lang="ts">

import { Component, Prop, Vue } from 'vue-property-decorator';

import NoPDFComponent from '../components/NoPDFComponent.vue'
import pdf from 'vue-pdf'

@Component({
	components: {
		Keypress: () => import('vue-keypress'),
		NoPDFComponent,
		pdf
	},
})

export default class ViewRulesViolations extends Vue {
	listElement = (document.getElementById('rulesViolations') as HTMLUListElement) ?
		document.getElementById('rulesViolations') as HTMLUListElement: undefined

	curPage = -1
	curLines = [Number(0)]

	mounted() {
		const locale = this.$route.query.locale as string
		if (locale != undefined && ['en', 'ru'].includes(locale)) {
			this.$i18n.locale = locale
		}
		if (document.getElementById('rulesViolations') == null) {
			setTimeout(this.mounted, 50)
			return;
		}
		if (!this.listElement) {
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
		const lines = ((li.textContent.match(RegExp("[0-9]+-[0-9]+")) != null) ?
      li.textContent!.match(RegExp("[0-9]+-[0-9]+"))![0].split("-") :
      [li.textContent!.split(this.$t('page.rulesViolations.line')+' ')[1]
        .split(', '+this.$t('page.rulesViolations.page'))[0]])
      .map((line) => Number(line.trim())).sort((a, b) => a - b)

		this.curPage = page
		this.curLines = lines
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
