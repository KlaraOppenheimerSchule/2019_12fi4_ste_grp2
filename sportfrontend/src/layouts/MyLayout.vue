<template>
  <q-layout view="hHh lpR fFf">
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-toolbar-title>
          <q-avatar rounded class="bg-white" elevated>
            <img src="~assets/logo-klara.png" />
          </q-avatar>
          <router-link :to="{ path: '/'}" class="text-white hide-underline">&nbsp;KlarAktiv</router-link>
        </q-toolbar-title>

        <q-btn dense flat round icon="menu" v-if="type > 0" @click="right = !right" />
        <q-btn dense flat round v-if="type == 0" to="/login">Login</q-btn>
      </q-toolbar>
    </q-header>

    <q-drawer v-model="right" side="right" overlay elevated>
      <!-- drawer content -->
      <div class="full-height column">
        <q-list v-if="type == 2">
          <q-item clickable :to="{ path: '/admin/class' }">
            <q-item-section>Klassen bearbeiten</q-item-section>
          </q-item>
          <q-separator />
          <q-item clickable :to="{ path: '/admin/ill' }">
            <q-item-section>Schüler austragen</q-item-section>
          </q-item>
          <q-separator />
          <q-item clickable :to="{ path: '/admin/checkpoints' }">
            <q-item-section>Stationen bearbeiten</q-item-section>
          </q-item>
        </q-list>
        <q-list v-if="type == 1">
          <q-item clickable :to="{ path: '/checkpoint/' + this.checkpoint }">
            <q-item-section>Zur Station</q-item-section>
          </q-item>
        </q-list>
        <q-space />
        <div class="q-pa-md">
          <q-btn color="primary" class="full-width" @click="logout()">Logout</q-btn>
        </div>
      </div>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
export default {
  data() {
    return {
      right: false,
      token: "",
      type: 0,
      id: 0,
      checkpoint: 0
    };
  },
  methods: {
    getToken: function() {
      let res = "";

      res = this.$q.cookies.get("token");
      if (res == undefined) {
        res = "";
      }

      this.token = res;
    },
    getType: function() {
      let res = 0;
      res = this.$q.cookies.get("type");
      if (res == undefined) {
        res = 0;
      }
      this.type = res;
    },
    logout: function() {
      let apiurl = this.$api + "session/" + this.token + "/destroy";

      this.$axios.get(apiurl).then(response => {
        let resdata = response.data;
        this.$q.cookies.remove("token");
        this.$q.cookies.remove("type");
        this.$router.push({ path: "/login" });
      });
    },
    getCheckpoint: function() {
      let checkpoint = this.$q.cookies.get("checkpoint");
      this.checkpoint = checkpoint;
    }
  },
  mounted: function() {
    this.getToken();
    this.getType();
    this.getCheckpoint();
  },
  updated: function() {
    this.getToken();
    this.getType();
    this.getCheckpoint();
  }
};
</script>

<style lang="scss">
.q-page {
  max-width: 900px;
  margin-left: auto;
  margin-right: auto;
}

.hide-underline {
  text-decoration: none;
}
</style>