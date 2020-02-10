import Vue from 'vue'
import VueRouter from 'vue-router'
// @/ is from webpack vue, see: https://stackoverflow.com/questions/42749973/es6-import-using-at-sign-in-path-in-a-vue-js-project-using-webpack
import ChatClient from '@/views/ChatClient.vue'
import About from '@/views/About.vue'

Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    component: ChatClient
  },
  {
    path: '/about',
    component: About
  }
];

const router = new VueRouter({
  routes
});

export default router
