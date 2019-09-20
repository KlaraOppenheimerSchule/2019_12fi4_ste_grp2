import Vue from "vue";
import Router from "vue-router";
import Main from "./views/Main.vue";
import StatsId from "./views/StatsId.vue";

Vue.use(Router);

export default new Router({
  mode: "history",
  base: process.env.BASE_URL,
  routes: [
    {
      path: "/",
      name: "main",
      component: Main
    },
    {
      path: "/about",
      name: "about",
      // route level code-splitting
      // this generates a separate chunk (about.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () =>
        import(/* webpackChunkName: "about" */ "./views/About.vue")
    },
    {
      path: "/stats/:id",
      name: "statsId",
      component: StatsId
    }
    //TODO: Folgende Routen ergänzen: ZettelID, Large Results, Station, Authentication, Klassen eintragen, Admin Dashboard, Neuen Eintrag ergänzen
  ]
});
