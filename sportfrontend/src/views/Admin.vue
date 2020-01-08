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
        <ion-col> </ion-col>
      </ion-row>
    </ion-grid>
  </ion-content>
</template>

<script>
export default {
  name: "admin",
  mounted: function() {
    let token = this.$cookie.get("token");
    let type = this.$cookie.get("type");

    if (token == undefined || type == undefined) {
      this.$router.push({ path: "login" });
    }

    /*axios.get(apiurl).then(response => {
      let resdata = response.data;
    }*/
  }
};
</script>
