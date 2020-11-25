import Vue from 'vue'
import App from './App.vue'
import lottie from 'vue-lottie'
import globalVariables from './global'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.config.productionTip = false
Vue.prototype.GLOBAL = globalVariables

Vue.use(ElementUI)
Vue.component('lottie', lottie)

new Vue({
  render: h => h(App),
}).$mount('#app')
