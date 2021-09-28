import Vue from 'vue';
import VueRouter, { RouteConfig } from 'vue-router';
import Home from '../views/Home.vue';

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
	{
		path: '/',
		name: 'Home',
		component: Home,
	},
	{
		path: '/viewPDF',
		name: 'ViewPDF',
		// route level code-splitting
		// this generates a separate chunk (about.[hash].js) for this route
		// which is lazy-loaded when the route is visited.
		component: () => import(/* webpackChunkName: "about" */ '../views/ViewPDF.vue'), 
		props: (route) => ({ pdfName: route.query.pdfName })
	},
];

const router = new VueRouter({
	base: "/app/",
	routes,
});

export default router;
