<template>
  <ion-content class="main">
    <ion-grid>
      <ion-row>
        <ion-col>
          <ion-text color="primary">
            <h2>Schüler austragen</h2>
          </ion-text>
        </ion-col>
      </ion-row>
      <ion-row>
        <ion-col>
          <ion-item>
            <ion-label position="floating">Schülernummer</ion-label>
            <ion-input
              class="numberInput"
              type="tel"
              :value="val"
              @input="inputevent($event.target)"
              maxlength="6"
            ></ion-input>
          </ion-item>
          <ion-text v-if="error" color="danger">{{ errortext }}</ion-text>
          <ion-text v-if="success" color="success"
            >Erfolgreich ausgetragen</ion-text
          >
          <ion-button size="full" v-on:click="austragen()"
            >Schüler austragen</ion-button
          >
        </ion-col>
      </ion-row>
    </ion-grid>
  </ion-content>
</template>

<script>
import axios from "axios";
export default {
  name: "ill",
  data: function() {
    return {
      val: null,
      error: false,
      errortext: "",
      success: false
    };
  },
  mounted: function() {
    //TODO: Move this to a seperate script
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
  },
  methods: {
    inputevent: function(target) {
      this.val = target.value;
    },
    validateInput: function() {
      let validateVar = false;
      if (this.val.toString().length == 6) {
        validateVar = true;
      }
      return validateVar;
    },
    austragen: function() {
      let token = this.$cookie.get("token");

      console.log(this.val);

      if (this.validateInput()) {
        let apiurl =
          process.env.VUE_APP_API_URL +
          "student/" +
          this.val +
          "/present/" +
          token +
          "/false";

        axios.get(apiurl).then(response => {
          let resdata = response.data;
          if (resdata[0].error != undefined) {
            this.errortext = resdata[0].error;
            this.error = true;
          } else {
            this.error = false;
            this.success = true;
          }
          console.log(resdata);
        });
      } else {
        this.errortext = "Schülernummer ist nicht gültig";
        this.error = true;
      }
    }
  }
};
</script>

<style lang="scss" scoped></style>
