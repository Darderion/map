<template>
	<div id="app">
		<NavbarComponent/>
		<router-view/>
	</div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { BootstrapVue } from 'bootstrap-vue'

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
	updatePdfName(): void {
		// this.$store.dispatch("setPdfName", this.$route.query.pdfName? (this.$route.query.pdfName as string): "")
		fetch(`${this.$store.getters.getAPI}getPDFSize?pdfName=${this.$route.query.pdfName}`)
			.then(numPages => {
				this.$store.commit('setNumPages', {
					pages: numPages
				})
			});
	}

	updated(): void {
		if (this.$store.getters.getNumPages == 0) {
			fetch(`${this.$store.getters.getAPI}getPDFSize?pdfName=${this.$route.query.pdfName}`)
				.then(response => response.json())
				.then(numPages => {
					this.$store.commit('setNumPages', {
						pages: numPages
					})
				});
			fetch(`${this.$store.getters.getAPI}viewRuleViolations?pdfName=${this.$route.query.pdfName}`)
				.then(response => response.json())
				.then(ruleViolations => {
					this.$store.commit('setRuleViolations', {
						ruleViolations
					})
					console.log(ruleViolations)
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
	background-color: #f7f7f7;
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
