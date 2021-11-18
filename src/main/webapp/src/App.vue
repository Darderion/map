<template>
	<div id="app">
		<NavbarComponent :setPdfName="setPdfName" :getPdfName="getPdfName" :getNumPages="getNumPages" :setNumPages="setNumPages"/>
		<router-view/>
	</div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';

import NavbarComponent from './components/Navbar.vue'

import pdf from 'vue-pdf'

@Component({
	components: {
		pdf,
		NavbarComponent
	},
})

export default class AppComponent extends Vue {
	@Prop() private pdfName = ""
	numPages = 0

	updatePdfName() {
		this.setPdfName(this.$route.query.pdfName? (this.$route.query.pdfName as string): "")
		fetch(`api/getPDFSize?pdfName=${this.$route.query.pdfName}`)
			.then(numPages => {
				this.numPages = Number(numPages)
			});
	}

	setPdfName(name: string) {
		this.pdfName = name
	}

	setNumPages(pages: number) {
		this.numPages = pages
	}

	getNumPages(): number {
		return this.numPages
	}

	getPdfName(): string {
		return this.pdfName
	}

	printPdfName(): void {
		console.log(this.pdfName)
	}

	updated() {
		this.updatePdfName()
	}
}
</script>

<style lang="scss">
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
