const routes = [
  {
    path: "/",
    component: () => import("layouts/MyLayout.vue"),
    children: [{ path: "", component: () => import("pages/Index.vue") }]
  },
  {
    path: "/stats/:id",
    component: () => import("layouts/MyLayout.vue"),
    children: [{ path: "", component: () => import("pages/StatsId.vue") }]
  },
  {
    path: "/login",
    component: () => import("layouts/MyLayout.vue"),
    children: [{ path: "", component: () => import("pages/Login.vue") }]
  },
  {
    path: "/checkpoint/:id",
    component: () => import("layouts/MyLayout.vue"),
    children: [{ path: "", component: () => import("pages/Checkpoint.vue") }]
  },
  {
    path: "/admin",
    component: () => import("layouts/MyLayout.vue"),
    children: [{ path: "", component: () => import("pages/Admin.vue") }]
  },
  {
    path: "/admin/class",
    component: () => import("layouts/MyLayout.vue"),
    children: [{ path: "", component: () => import("pages/AddClass.vue") }]
  },
  {
    path: "/admin/ill",
    component: () => import("layouts/MyLayout.vue"),
    children: [{ path: "", component: () => import("pages/Ill.vue") }]
  },
  {
    path: "/admin/checkpoints",
    component: () => import("layouts/MyLayout.vue"),
    children: [{ path: "", component: () => import("pages/EditCheckpoints.vue") }]
  },
  {
    path: "/admin/print/:id",
    component: () => import("layouts/printlayout.vue"),
    children: [{ path: "", component: () => import("pages/Print.vue") }]
  }
];

// Always leave this as last one
if (process.env.MODE !== "ssr") {
  routes.push({
    path: "*",
    component: () => import("pages/Error404.vue")
  });
}

export default routes;
