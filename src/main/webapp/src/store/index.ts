
import RuleViolation from '@/classes/RuleViolation'
import Vue from 'vue'
import Vuex from 'vuex'
import lines from './modules/PDFLines'

Vue.use(Vuex)

export default new Vuex.Store({
	state: {
		pdfName: "",
		pages: 0,
		ruleViolations: []
	},
	mutations: {
		setPdfName(state, payload) {
			state.pdfName = payload.name
		},
		setNumPages(state, payload) {
			state.pages = payload.pages
		},
		setRuleViolations(state, payload) {
			state.ruleViolations = payload.ruleViolations
		}
	},
	actions: {},
	getters: {
		getPdfName(state): string {
			return state.pdfName
		},
		getNumPages(state): number {
			return state.pages
		},
		getRuleViolations(state): RuleViolation[] {
			return state.ruleViolations
		}
	},

	modules: {
		lines
	}
})
