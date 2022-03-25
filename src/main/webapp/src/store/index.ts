
import RuleViolation from '@/classes/RuleViolation'
import Vue from 'vue'
import Vuex from 'vuex'
import lines from './modules/PDFLines'

Vue.use(Vuex)

export default new Vuex.Store({
	state: {
		pdfName: "",
		pages: 0,
		ruleViolations: [],
		noErrorsFound: false,
		noAreaErrorsFound: false,
		languages: ['ru', 'en'],
		fileNames: []
	},
	mutations: {
		setPdfName(state, payload) {
			state.pdfName = payload.name
		},
		setNumPages(state, payload) {
			state.pages = payload.pages
		},
		setFileNames(state, payload) {
			state.fileNames = payload.fileNames;
		},
		setRuleViolations(state, payload) {
			state.ruleViolations = payload.ruleViolations
		},
		updateContainsErrors(state, payload) {
			state.noErrorsFound = payload.ruleViolations.length == 0
			state.noAreaErrorsFound =
			payload.ruleViolations.length == 0 ||
			payload.ruleViolations.filter((ruleViolation: RuleViolation) => ruleViolation.message == 'PDFArea').length == 0
		}
	},
	actions: {},
	getters: {
		getLanguages(state): string[] {
			return state.languages;
		},
		getFileNames(state): string[] {
			return state.fileNames;
		},
		pdfName(state): boolean {
			return state.pdfName != undefined && state.pdfName.trim() != "" && state.pdfName != "undefined"
		},
		getContainsErrors(state): boolean {
			return !state.noErrorsFound
		},
		getContainsAreaErrors(state): boolean {
			return !state.noAreaErrorsFound
		},
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
