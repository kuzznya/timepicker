import Vue from 'vue'
import App from './App.vue'
import axios from "./axios"
import router from './router'
import store from './store'
import '@/plugins/bootstrap-vue'
import '@/plugins/calendar'
import VueKeycloak from "@dsb-norge/vue-keycloak-js";

Vue.config.productionTip = false

Vue.prototype.$store = store
Vue.prototype.$axios = axios

const apiHost = process.env.VUE_APP_AUTH_PATH || "AUTH_PATH"

Vue.use(VueKeycloak, {
  config: {
    url: `${apiHost}/auth`,
    realm: 'timepicker',
    clientId: 'timepicker-frontend'
  },
  init: {
    onLoad: 'check-sso'
  },
  logout: {
    redirectUri: window.location.origin
  },
  onReady: kc => start(kc)
})

/**
 * @param keycloak {KeycloakInstance}
 */
function initTokenInterceptor(keycloak) {
  axios.interceptors.request.use(config => {
    if (keycloak.authenticated) {
      config.headers.Authorization = `Bearer ${Vue.prototype.$keycloak.token}`
    }
    return config
  }, error => {
    return Promise.reject(error)
  })
}

/**
 * @param keycloak {KeycloakInstance}
 */
function initRouterGuard(keycloak) {
  router.beforeEach((to, from, next) => {
    if (to.meta.secured && !keycloak.authenticated) keycloak.login()
    return next()
  })
}

/**
 * @param keycloak {KeycloakInstance}
 */
function start(keycloak) {
  initRouterGuard(keycloak)
  initTokenInterceptor(keycloak)
  new Vue({
    router,
    store,
    render: h => h(App)
  }).$mount('#app')
}
