<template>
  <q-page class="q-gutter-md column q-pa-md">
    <div class="column">
      <div class="text-h4 text-primary">Administration</div>
    </div>
    <div class="column">
      <router-link :to="{ path: '/admin/class' }">Klassen bearbeiten</router-link>
      <br />
      <router-link :to="{ path: '/admin/ill' }">Schüler austragen</router-link>
      <br />
      <router-link :to="{ path: '/admin/checkpoints' }">Stationen bearbeiten</router-link>
    </div>
  </q-page>
</template>

<script>
export default {
  name: "admin",
  mounted: function() {
    let token = this.$q.cookies.get("token");
    let type = this.$q.cookies.get("type");

    if (token == undefined || type == undefined) {
      this.$router.push({ name: "login" });
    }

    let apiurl = this.$api + "session/" + token + "/validate";

    this.$axios.get(apiurl).then(response => {
      let resdata = response.data;
      if (resdata[0].valid != true) {
        this.$router.push({ path: "/login" });
      }
    });
  }
};
</script>
