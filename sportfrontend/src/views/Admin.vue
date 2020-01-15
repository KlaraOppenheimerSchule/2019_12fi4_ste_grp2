<template>
  <ion-content class="main">
    <ion-grid>
      <ion-row>
        <ion-col>
          <ion-text color="primary">
            <h2>Administration</h2>
          </ion-text>
        </ion-col>
      </ion-row>
      <ion-row>
        <ion-col>
          <router-link to="/admin/class">Klassen bearbeiten</router-link>
          <div>Schüler austragen</div>
          <div>Stationen bearbeiten</div>
        </ion-col>
      </ion-row>
    </ion-grid>
  </ion-content>
</template>

<script>
import axios from "axios";
export default {
  name: "admin",
  mounted: function() {
    let token = this.$cookie.get("token");
    let type = this.$cookie.get("type");

    if (token == undefined || type == undefined) {
      this.$router.push({ path: "login" });
    }

    let apiurl = process.env.VUE_APP_API_URL + "session/" + token + "/validate";

    axios.get(apiurl).then(response => {
      let resdata = response.data;
      if (resdata[0].valid != true) {
        this.$router.push({ path: "login" });
      }
    });
  }
};
</script>
