import Vue from "vue";
import { IonicVueRouter } from "@ionic/vue";
import Home from "./views/Home.vue";
import Admin from "./views/Admin.vue";
import Checkpoint from "./views/Checkpoint.vue";
import AddClass from "./views/AddClass.vue";
import EditCheckpoints from "./views/EditCheckpoints.vue";
import Ill from "./views/Ill.vue";
import Print from "./views/Print.vue";
import Login from "./views/Login.vue";
import StatsId from "./views/StatsId.vue";

Vue.use(IonicVueRouter);

export default new IonicVueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes: [
    {
      path: "/",
      name: "home",
      component: Home
    },
    {
      path: "/login",
      name: "login",
      component: Login
    },
    {
      path: "/checkpoint",
      name: "checkpoint",
      component: Checkpoint
    },
    {
      path: "/admin",
      name: "admin",
      component: Admin
    },
    {
      path: "/admin/class",
      name: "addclass",
      component: AddClass
    },
    {
      path: "/admin/ill",
      name: "ill",
      component: Ill
    },
    {
      path: "/admin/checkpoints",
      name: "editcheckpoints",
      component: EditCheckpoints
    },
    {
      path: "/admin/print/:id",
      name: "print",
      component: Print,
      props: true
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
