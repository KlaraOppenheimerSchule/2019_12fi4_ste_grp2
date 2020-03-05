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
          <router-link :to="{ name: 'addclass' }"
            >Klassen bearbeiten</router-link
          >
          <br />
          <router-link :to="{ name: 'ill' }">Schüler austragen</router-link>
          <br />
          <router-link :to="{ name: 'editcheckpoints' }"
            >Stationen bearbeiten</router-link
          >
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
      this.$router.push({ name: "login" });
    }

    let apiurl = process.env.VUE_APP_API_URL + "session/" + token + "/validate";

    axios.get(apiurl).then(response => {
      let resdata = response.data;
      if (resdata[0].valid != true) {
        this.$router.push({ name: "login" });
      }
    });
  }
};
</script>
