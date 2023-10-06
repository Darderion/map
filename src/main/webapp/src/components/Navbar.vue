
<template>
	<div id="navbar">
		<!--
		<UploadFileComponent id="uploadFileComponent"/>
		-->
		<MenuComponent id="menuComponent"/>
		<div id="div_i18n">
			<a id="div_i18n_1" href="#" @click="setLocale('en')"><CountryFlag country='gb' size='big'/></a>
			<a id="div_i18n_2" href="#" @click="setLocale('ru')"><CountryFlag country='ru' size='big'/></a>
		</div>
	</div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

import UploadFileComponent from './UploadFile.vue'
import MenuComponent from './Menu.vue'
import CountryFlag from 'vue-country-flag'

@Component({
	components: {
		UploadFileComponent,
		MenuComponent,
		CountryFlag
	},
})

export default class NavbarComponent extends Vue {
	setLocale(locale: string): void {
		this.$i18n.locale = locale;
	}
}
</script>

<style scoped>
#navbar {
	display: grid;
	grid-template-columns: 10% 80% 10%;
}
#menuComponent {
	grid-column: 2;
}
#uploadFileComponent {
	float: left;
	margin-left: 5%;
}
#div_i18n {
	margin: auto;
	display: grid;
	grid-template-columns: 40% 20% 40%;
	border: solid 24px #abc;
	background: #abc;
}
#div_i18n_2 {
	grid-column: 3;
}
</style>
