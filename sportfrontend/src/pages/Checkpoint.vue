<template>
  <q-page class="column q-gutter-md q-pa-md">
    <div class="text-h4 text-primary">Station</div>
  </q-page>
</template>

<script>
export default {
  name: "admin",
  mounted: function() {
    let token = this.$q.cookies.get("token");
    let type = this.$q.cookies.get("type");

    if (token == undefined || type == undefined) {
      this.$router.push({ path: "/login" });
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
