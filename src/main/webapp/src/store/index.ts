
import Vue from 'vue'
import Vuex from 'vuex'
import lines from './modules/PDFLines'

Vue.use(Vuex)

export default new Vuex.Store({
	state: {
		pdfName: "",
		pages: 0
	},
	mutations: {
		setPdfName(state, payload) {
			console.log('setPdfName')
			console.log(payload.name)
			state.pdfName = payload.name
		},
		setNumPages(state, payload) {
			console.log('setNumPages')
			console.log(payload.pages)
			state.pages = payload.pages
		}
	},
	actions: {},
	getters: {
		getPdfName(state): string {
			console.log('getPdfName')
			console.log(state)
			return state.pdfName
		},
		getNumPages(state): number {
			console.log('getNumPages')
			console.log(state)
			return state.pages
		}
	},

	modules: {
		lines
	}
})
