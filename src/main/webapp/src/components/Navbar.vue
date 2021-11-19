
<template>
	<div id="navbar">
		<UploadFileComponent id="uploadFileComponent"/>
		<MenuComponent id="menuComponent"/>
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
}
</script>

<style scoped>
#uploadFileComponent {
	float: left;
	margin-left: 5%;
}
</style>
