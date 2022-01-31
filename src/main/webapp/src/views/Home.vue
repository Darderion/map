<template>
	<div class="home">
		<br>Инструкция по использованию:
		<br>
		<br>Для начала работы выберите PDF файл курсовой/дипломной работы, нажав на кнопку <b><i>"Select File"</i></b>
		<br>
		<br>Просмотреть PDF можно в автоматически открывшейся после загрузки PDF вкладке <b><i>"View PDF"</i></b>
		<br>
		<br>Просмотреть список ошибок, найденных в работе, можно во вкладке <b><i>"Rules' violations"</i></b>
		<br>
		<br>
		<br>
		<br>Список проверяемых ошибок оформления:
		<br>
		<br><div style="width: 50%; margin: auto;"><ul>
			<li>Один подраздел в разделе</li>
			<li>Неправильное использование дефиса</li>
			<li>Неправильное использование короткого тире</li>
			<li>Неправильное использование длинного тире</li>
			<li>Некорректные ссылки на литературу [?]</li>
			<li>Введение, заключение и список литературы не нумеруются</li>
			</ul>
			</div>
	</div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component({
	components: {
	},
})
export default class Home extends Vue {}
</script>

<style scoped>
div {
	font-size: 24px;
}
</style>
