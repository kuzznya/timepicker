import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '@/views/Home.vue'
import Profile from "@/views/Profile";
import EventInfo from "@/views/EventInfo";

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: {
      secured: true
    }
  },
  {
    path: '/events',
    name: 'Events',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "events" */ '@/views/Events.vue'),
    meta: {
      secured: true
    }
  },
  {
    path: '/events/:id',
    name: 'EventInfo',
    component: EventInfo,
    props: route => ({
      id: route.params.id
    }),
    meta: {
      secured: true
    }
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
