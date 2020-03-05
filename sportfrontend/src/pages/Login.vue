<template>
  <q-page class="q-pa-md column q-gutter-md">
    <div class="text-h4">Login</div>
    <div class="column">
      <q-input label="Username" class="userInput" type="text" v-model="user"></q-input>
      <q-input label="Passwort" class="passInput" type="password" v-model="pass"></q-input>
      <q-btn :disabled="!isEnabled" class="full-width" @click="login()">Login</q-btn>
      <div class="text-body2 text-danger" v-if="error != ''" v-html="error"></div>
    </div>
  </q-page>
</template>

<script>
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
    login: async function() {
      let apiurl = this.$api + "session/create/" + this.user + "/";

      let shapass = await this.sha256(this.pass);

      apiurl = apiurl + shapass;

      this.$axios.get(apiurl).then(response => {
        let resdata = response.data;
        if (resdata[0].token != undefined) {
          this.$q.cookies.set("token", resdata[0].token, { expires: "1D" });
          this.$q.cookies.set("type", resdata[0].type, { expires: "1D" });
          console.log(resdata[0]);
          this.$store.commit("logIn");
          this.redirectCookie(resdata[0]);
        } else {
          this.error = resdata[0].error;
        }
      });
    },

    getUserID: async function(token) {
      let apiurl = this.$api + "session/" + token + "/validate";

      let id = await this.$axios.get(apiurl).then(response => {
        let resdata = response.data;
        console.log(resdata);
        return resdata[0].user;
      });

      return id;
    },

    redirectCheckpoint: async function(token) {
      let id = await this.getUserID(token);
      console.log(id);
      let apiurl = this.$api + "user/" + id + "/checkpoints/" + token;

      this.$axios.get(apiurl).then(response => {
        let resdata = response.data;
        
        let id = resdata[0].checkpoints[0].id;
        this.$router.push({ path: "/checkpoint/" + id });

      });
    },
    redirectCookie: function(response) {
      let cookieType = response.type;
      switch (cookieType) {
        case "2":
          this.$router.push({ path: "/admin" });
          break;

        case "1":
          this.redirectCheckpoint(response.token);
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
