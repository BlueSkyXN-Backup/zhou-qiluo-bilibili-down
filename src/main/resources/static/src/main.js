import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import axios from 'axios';
axios.defaults.baseURL = 'http://localhost:8686';
Vue.config.productionTip = false
Vue.use(ElementUI);

new Vue({
  render: function (h) { return h(App) },
}).$mount('#app')
