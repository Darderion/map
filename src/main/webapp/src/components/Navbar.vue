
<template>
	<div id="navbar">
		<UploadFileComponent id="uploadFileComponent" :setPdfName="setPdfName" :setNumPages="setNumPages"/>
		<MenuComponent id="menuComponent" :getPdfName="getPdfName" :getNumPages="getNumPages"/>
	</div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';

import UploadFileComponent from './UploadFile.vue'
import MenuComponent from './Menu.vue'

@Component({
	components: {
		UploadFileComponent,
		MenuComponent
	},
})

export default class NavbarComponent extends Vue {
	@Prop() setPdfName!: (name: string) => void
	@Prop() setNumPages!: (pages: number) => void
	@Prop() getPdfName!: () => string
	@Prop() getNumPages!: () => number
}
</script>

<style scoped>
#uploadFileComponent {
	float: left;
	margin-left: 5%;
}
</style>
