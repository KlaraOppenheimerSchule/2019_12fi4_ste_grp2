import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import "./registerServiceWorker";
import Ionic from "@ionic/vue";
import "@ionic/core/css/core.css";

Vue.config.productionTip = false;
Vue.use(Ionic);

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app");
