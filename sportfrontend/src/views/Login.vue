<template>
  <ion-content class="main">
    <ion-grid>
      <ion-row>
        <ion-col>
          <ion-text color="primary">
            <h2>Login</h2>
          </ion-text>
        </ion-col>
      </ion-row>
      <ion-row>
        <ion-col>
          <ion-item>
            <ion-label position="floating">Username</ion-label>
            <ion-input
              class="userInput"
              type="text"
              :value="user"
              @input="inputevent($event.target, 'user')"
            ></ion-input>
          </ion-item>
          <ion-item>
            <ion-label position="floating">Passwort</ion-label>
            <ion-input
              class="passInput"
              type="password"
              :value="pass"
              @input="inputevent($event.target, 'pass')"
            ></ion-input>
          </ion-item>
          <ion-button :disabled="!isEnabled" size="full" v-on:click="login()"
            >Login</ion-button
          >
          <ion-text color="danger" v-if="error != ''" v-html="error"></ion-text>
        </ion-col>
      </ion-row>
    </ion-grid>
  </ion-content>
</template>

<script>
import axios from "axios";

export default {
  name: "login",
  data: function() {
    return {
      user: "",
      pass: "",
      error: ""
    };
  },
  methods: {
    inputevent: function(target, property) {
      if (property === "user") {
        this.user = target.value;
      }
      if (property === "pass") {
        this.pass = target.value;
      }
    },
    login: async function() {
      let apiurl =
        process.env.VUE_APP_API_URL + "session/create/" + this.user + "/";

      let shapass = await this.sha256(this.pass);

      apiurl = apiurl + shapass;

      axios.get(apiurl).then(response => {
        let resdata = response.data;
        if (resdata[0].token != undefined) {
          this.$cookie.set("token", resdata[0].token, { expires: "1D" });
          this.$cookie.set("type", resdata[0].type, { expires: "1D" });
          this.$store.commit("logIn");
          this.redirectCookie();
        } else {
          this.error = resdata[0].error;
        }
      });
    },
    redirectCookie: function() {
      let cookieType = this.$cookie.get("type");
      switch (cookieType) {
        case "2":
          this.$router.push({ path: "admin" });
          break;

        case "1":
          this.$router.push({ path: "station" });
          break;

        default:
          this.$router.push({ path: "/" });
          break;
      }
    },
    sha256: async function(message) {
      // encode as UTF-8
      const msgBuffer = new TextEncoder("utf-8").encode(message);
      // hash the message
      const hashBuffer = await crypto.subtle.digest("SHA-256", msgBuffer);
      // convert ArrayBuffer to Array
      const hashArray = Array.from(new Uint8Array(hashBuffer));

      // convert bytes to hex string
      const hashHex = hashArray
        .map(b => ("00" + b.toString(16)).slice(-2))
        .join("");
      return hashHex;
    }
  },
  computed: {
    isEnabled: function() {
      let res = false;
      if (this.user != "" && this.pass != "") {
        res = true;
      }
      return res;
    }
  }
};
</script>
