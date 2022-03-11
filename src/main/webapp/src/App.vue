<template>
	<div id="app">
		<NavbarComponent/>
		<router-view/>
	</div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'

import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

Vue.use(BootstrapVue)

import NavbarComponent from './components/Navbar.vue'

import pdf from 'vue-pdf'

@Component({
	components: {
		Keypress: () => import('vue-keypress'),
		pdf,
		NavbarComponent
	},
})

export default class AppComponent extends Vue {
	updatePdfName() {
		// this.$store.dispatch("setPdfName", this.$route.query.pdfName? (this.$route.query.pdfName as string): "")
		fetch(`api/getPDFSize?pdfName=${this.$store.getters.getPdfName}`)
			.then(numPages => {
				this.$store.commit('setNumPages', {
					pages: numPages
				})
			});
	}

	updated() {
		this.$store.commit('setPdfName', {
			name: this.$route.query.pdfName
		})

		if (this.$store.getters.getNumPages == 0) {
			fetch(`api/getPDFSize?pdfName=${this.$store.getters.getPdfName}`)
				.then(response => response.json())
				.then(numPages => {
					this.$store.commit('setNumPages', {
						pages: numPages
					})
				});
			fetch(`api/viewRuleViolations?pdfName=${this.$store.getters.getPdfName}`)
				.then(response => response.json())
				.then(ruleViolations => {
					this.$store.commit('setRuleViolations', {
						ruleViolations
					})
					this.$store.commit('updateContainsErrors', {
						ruleViolations
					})
				});
		}
	}
}
</script>

<style lang="scss">
body {
	background-color: #abc;
}

#app {
	font-family: Avenir, Helvetica, Arial, sans-serif;
	-webkit-font-smoothing: antialiased;
	-moz-osx-font-smoothing: grayscale;
	text-align: center;
	color: #2c3e50;
}

#nav {
	padding: 30px;

	a {
		font-weight: bold;
		color: #2c3e50;

		&.router-link-exact-active {
			color: #42b983;
		}
	}
}
</style>
