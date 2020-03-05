<template>
  <ion-content class="main">
    <ion-grid>
      <ion-row>
        <ion-col>
          <ion-text color="primary">
            <h2>Stationen bearbeiten</h2>
          </ion-text>
        </ion-col>
      </ion-row>
      <ion-row>
        <ion-col>
          <table>
            <tbody>
              <template v-for="checkpoint in checkpoints">
                <tr class="checkpointitem" :key="checkpoint.id">
                  <td colspan="3" class="checkpointlabel">
                    {{ checkpoint.name }}
                  </td>
                  <td class="checkpointlabel">
                    {{ checkpoint.userdata.name }}
                  </td>
                  <td>
                    <ion-button v-on:click="toggleEdit(checkpoint)"
                      >bearbeiten</ion-button
                    >
                  </td>
                  <!--TODO: Implement this -->
                </tr>
                <tr :key="checkpoint.id + 'input'" v-if="checkpoint.edit">
                  <td>
                    <ion-input
                      :value="checkpoint.name"
                      :id="'checkpoint-input-name-' + checkpoint.id"
                    ></ion-input>
                  </td>
                  <td>
                    <ion-input
                      :value="checkpoint.location"
                      :id="'checkpoint-input-location-' + checkpoint.id"
                    ></ion-input>
                  </td>
                  <td>
                    <ion-input
                      type="password"
                      placeholder="password"
                      :id="'checkpoint-input-password-' + checkpoint.id"
                      required
                    ></ion-input>
                  </td>
                  <td>
                    <ion-input
                      type="number"
                      :value="checkpoint.score"
                      :id="'checkpoint-input-score-' + checkpoint.id"
                    ></ion-input>
                  </td>
                  <td>
                    <ion-button v-on:click="sendEdit(checkpoint)"
                      >speichern</ion-button
                    >
                    <ion-button
                      color="danger"
                      v-on:click="deleteCheckpoint(checkpoint)"
                      >löschen</ion-button
                    >
                  </td>
                </tr>
                <tr :key="checkpoint.id + 'hr'">
                  <td colspan="5">
                    <hr />
                  </td>
                </tr>
              </template>
              <tr v-if="newUser.visible">
                <td colspan="2">
                  <ion-input
                    placeholder="Name"
                    id="newUserName"
                    name="newUserName"
                  ></ion-input>
                </td>
                <td>
                  <ion-input
                    placeholder="Location"
                    name="newUserLocation"
                    id="newUserLocation"
                  ></ion-input>
                </td>
                <td>
                  <ion-input
                    placeholder="Password"
                    type="password"
                    name="newUserPassword"
                    id="newUserPassword"
                  ></ion-input>
                </td>
                <td>
                  <ion-input
                    placeholder="Score"
                    type="number"
                    name="newUserScore"
                    id="newUserScore"
                  ></ion-input>
                </td>
              </tr>
            </tbody>
          </table>
        </ion-col>
      </ion-row>
      <ion-row>
        <ion-col>
          <ion-button v-on:click="newUserInput()">Neue Station</ion-button>
        </ion-col>
      </ion-row>
    </ion-grid>
  </ion-content>
</template>

<script>
import axios from "axios";
export default {
  name: "editcheckpoints",
  data: function() {
    return {
      checkpoints: [],
      newUser: {
        visible: false
      }
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

    this.getCheckpoints();
  },
  methods: {
    ///checkpoint
    //create
    //:id
    //delete
    //update
    ///user
    //create
    //:id
    //delete
    //update
    inputevent: function(target) {
      this.val = target.value;
    },
    clearNewInput: function() {
      document.getElementById("newUserName").value = "";
      document.getElementById("newUserLocation").value = "";
      document.getElementById("newUserPassword").value = "";
      document.getElementById("newUserScore").value = 1;
    },
    newUserInput: async function() {
      if (this.newUser.visible === false) {
        this.newUser.visible = !this.newUser.visible;
      } else {
        let name = document.getElementById("newUserName").value;
        let location = document.getElementById("newUserLocation").value;
        let password = document.getElementById("newUserPassword").value;
        let score = document.getElementById("newUserScore").value;

        if (
          name !== undefined &&
          location !== undefined &&
          password !== undefined &&
          score !== undefined
        ) {
          let shapass = await this.sha256(password);
          this.createCheckpoint(name, location, shapass, score);
        }
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
    },
    toggleEdit: function(checkpoint) {
      checkpoint.edit = !checkpoint.edit;
      this.$forceUpdate();
    },
    sendEdit: async function(checkpoint) {
      let id = checkpoint.id;

      let name = document.getElementById("checkpoint-input-name-" + id).value;
      let location = document.getElementById("checkpoint-input-location-" + id)
        .value;
      let password = document.getElementById("checkpoint-input-password-" + id)
        .value;
      let score = document.getElementById("checkpoint-input-score-" + id).value;

      let shapass = await this.sha256(password);

      await this.updateUser(checkpoint.user, name, shapass);
      await this.updateCheckpoint(id, name, location, checkpoint.user, score);

      this.getCheckpoints();
    },
    getCheckpoints: function() {
      let token = this.$cookie.get("token");
      let apiurl = process.env.VUE_APP_API_URL + "checkpoint/all/" + token;

      axios.get(apiurl).then(response => {
        let resdata = response.data;
        this.checkpoints = resdata;
        for (let i = 0; i < this.checkpoints.length; i++) {
          this.checkpoints[i].userdata = { name: "loading" };
          this.checkpoints[i].edit = false;
          this.getUser(this.checkpoints[i]);
        }
      });
    },
    getUser: function(checkpoint) {
      let apiurl = process.env.VUE_APP_API_URL + "user/" + checkpoint.user;

      axios.get(apiurl).then(response => {
        let resdata = response.data;
        checkpoint.userdata = resdata[0];
        this.$forceUpdate();
      });
    },
    createCheckpoint: async function(name, location, password, score) {
      let token = this.$cookie.get("token");

      let user = await this.createUser(name, password);
      console.log("checkpoint");

      let apiurl =
        process.env.VUE_APP_API_URL +
        "checkpoint/create/" +
        token +
        "/" +
        name +
        "/" +
        location +
        "/" +
        user +
        "/" +
        score;

      axios.get(apiurl).then(response => {
        let resdata = response.data;
        this.getCheckpoints();
        this.clearNewInput();
      });
    },
    createUser: async function(name, password) {
      let token = this.$cookie.get("token");

      let apiurl =
        process.env.VUE_APP_API_URL +
        "user/create/" +
        token +
        "/" +
        name +
        "/" +
        password;
      let res = await axios.get(apiurl).then(response => {
        let resdata = response.data;
        return resdata[0].user;
      });
      return res;
    },
    updateCheckpoint: async function(id, name, location, user, score) {
      let token = this.$cookie.get("token");

      let apiurl =
        process.env.VUE_APP_API_URL +
        "checkpoint/" +
        id +
        "/update/" +
        token +
        "/" +
        name +
        "/" +
        location +
        "/" +
        user +
        "/" +
        score;

      await axios.get(apiurl).then(response => {
        let resdata = response.data;
      });
    },
    updateUser: async function(id, name, password) {
      let token = this.$cookie.get("token");

      let apiurl =
        process.env.VUE_APP_API_URL +
        "user/" +
        id +
        "/update/" +
        token +
        "/" +
        name +
        "/" +
        password;
      await axios.get(apiurl).then(response => {
        let resdata = response.data;
      });
    },
    deleteUser: async function(id) {
      let token = this.$cookie.get("token");
      let apiurl =
        process.env.VUE_APP_API_URL + "user/" + id + "/delete/" + token;

      await axios.get(apiurl).then(response => {
        let resdata = response.data;
      });
    },
    deleteCheckpoint: async function(checkpoint) {
      let token = this.$cookie.get("token");

      let apiurl =
        process.env.VUE_APP_API_URL +
        "checkpoint/" +
        checkpoint.id +
        "/delete/" +
        token;

      await axios.get(apiurl).then(response => {
        let resdata = response.data;
      });
      
      await this.deleteUser(checkpoint.user);
      this.getCheckpoints();
    }
  }
};
</script>

<style lang="scss" scoped>
table {
  width: 100%;
}
</style>
