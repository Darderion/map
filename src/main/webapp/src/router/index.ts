import Vue from 'vue';
import VueRouter, { RouteConfig } from 'vue-router';
import Home from '../views/Home.vue';
import About from '../views/About.vue';

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
	{
		path: '/',
		name: 'Home',
		component: Home,
	},
	{
		path: '/about',
		name: 'About',
		component: About,
	},
	/*
	{
		path: '/viewPDFText',
		name: 'ViewPDFText',
		component: () => import('../views/ViewPDFText.vue'),
		props: (route) => ({ pdfName: route.query.pdfName })
	},
	*/
	{
		path: '/viewPDF',
		name: 'ViewPDF',
		component: () => import('../views/ViewPDF.vue'),
		props: (route) => ({ pdfName: route.query.pdfName })
	},
	{
		path: '/viewPDFText',
		name: 'ViewPDFText',
		component: () => import('../views/ViewPDFText.vue'),
		props: (route) => ({ pdfName: route.query.pdfName })
	},
	{
		path: '/viewPDFTextErrors',
		name: 'ViewPDFTextErrors',
		component: () => import('../views/ViewPDFTextErrors.vue'),
		props: (route) => ({ pdfName: route.query.pdfName })
	},
	{
		path: '/viewPDFTextSections',
		name: 'ViewPDFTextSections',
		component: () => import('../views/ViewPDFTextSections.vue'),
		props: (route) => ({ pdfName: route.query.pdfName })
	},
	{
		path: '/viewRulesViolations',
		name: 'ViewRulesViolations',
		component: () => import('../views/ViewRulesViolations.vue'),
		props: (route) => ({ pdfName: route.query.pdfName })
	},
	{
		path: '/uploadMultipleFiles',
		name: 'UploadMultipleFiles',
		component: () => import('../views/UploadMultipleFiles.vue'),
		props: (route) => ({ pdfName: route.query.pdfName })
	},
];

const router = new VueRouter({
	base: "/",
	routes,
});

export default router;
