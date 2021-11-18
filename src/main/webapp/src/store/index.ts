
import Vue from 'vue'
import Vuex from 'vuex'
import lines from './modules/PDFLines'

Vue.use(Vuex)

export default new Vuex.Store({
	actions: {},
	mutations: {},
	state: {},
	getters: {},

	modules: {
		lines
	}
})
