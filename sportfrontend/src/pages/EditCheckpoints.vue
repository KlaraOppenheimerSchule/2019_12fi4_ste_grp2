<template>
  <q-page class="column q-gutter-md q-pa-md">
    <div class="text-h4 text-primary">Stationen bearbeiten</div>
    <table>
      <tbody>
        <template v-for="checkpoint in checkpoints">
          <tr class="checkpointitem" :key="checkpoint.id">
            <td colspan="3" class="checkpointlabel">{{ checkpoint.name }}</td>
            <td class="checkpointlabel">{{ checkpoint.userdata.name }}</td>
            <td>
              <q-btn @click="toggleEdit(checkpoint)">bearbeiten</q-btn>
            </td>
            <!--TODO: Implement this -->
          </tr>
          <tr :key="checkpoint.id + 'input'" v-if="checkpoint.edit">
            <td>
              <q-input
                v-model="checkpoint.newName"
                :placeholder="checkpoint.name"
                :id="'checkpoint-input-name-' + checkpoint.id"
              ></q-input>
            </td>
            <td>
              <q-input
                v-model="checkpoint.newLocation"
                :placeholder="checkpoint.location"
                :id="'checkpoint-input-location-' + checkpoint.id"
              ></q-input>
            </td>
            <td>
              <q-input
                v-model="checkpoint.password"
                type="password"
                label="password"
                :id="'checkpoint-input-password-' + checkpoint.id"
                required
              ></q-input>
            </td>
            <td>
              <q-input
                v-model="checkpoint.newScore"
                type="number"
                :placeholder="checkpoint.score"
                :id="'checkpoint-input-score-' + checkpoint.id"
              ></q-input>
            </td>
            <td>
              <div class="row no-wrap">
                <q-btn v-on:click="sendEdit(checkpoint)">speichern</q-btn>
                <q-btn class="text-negative" @click="deleteCheckpoint(checkpoint)">löschen</q-btn>
              </div>
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
            <q-input placeholder="Name" id="newUserName" name="newUserName" v-model="newUser.name"></q-input>
          </td>
          <td>
            <q-input
              placeholder="Location"
              name="newUserLocation"
              id="newUserLocation"
              v-model="newUser.location"
            ></q-input>
          </td>
          <td>
            <q-input
              placeholder="Password"
              type="password"
              name="newUserPassword"
              id="newUserPassword"
              v-model="newUser.password"
            ></q-input>
          </td>
          <td>
            <q-input
              placeholder="Score"
              type="number"
              name="newUserScore"
              id="newUserScore"
              v-model="newUser.number"
            ></q-input>
          </td>
        </tr>
      </tbody>
    </table>

    <q-btn @click="newUserInput()">Neue Station</q-btn>
  </q-page>
</template>

<script>
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

      let name = checkpoint.newName;
      if (name == undefined) {
        name = checkpoint.name;
      }

      let location = checkpoint.newLocation;
      if (location == undefined) {
        location = checkpoint.location;
      }

      let password = checkpoint.password;

      let score = checkpoint.newScore;
      if (score == undefined) {
        score = checkpoint.score;
      }

      let shapass = await this.sha256(password);

      await this.updateUser(checkpoint.user, name, shapass);
      await this.updateCheckpoint(id, name, location, checkpoint.user, score);

      this.getCheckpoints();
    },
    getCheckpoints: function() {
      let token = this.$q.cookies.get("token");
      let apiurl = this.$api + "checkpoint/all/" + token;

      this.$axios.get(apiurl).then(response => {
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
      let apiurl = this.$api + "user/" + checkpoint.user;

      this.$axios.get(apiurl).then(response => {
        let resdata = response.data;
        checkpoint.userdata = resdata[0];
        this.$forceUpdate();
      });
    },
    createCheckpoint: async function(name, location, password, score) {
      let token = this.$q.cookies.get("token");

      let user = await this.createUser(name, password);

      let apiurl =
        this.$api +
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

      this.$axios.get(apiurl).then(response => {
        let resdata = response.data;
        this.getCheckpoints();
        this.clearNewInput();
      });
    },
    createUser: async function(name, password) {
      let token = this.$q.cookies.get("token");

      let apiurl =
        this.$api + "user/create/" + token + "/" + name + "/" + password;
      let res = await this.$axios.get(apiurl).then(response => {
        let resdata = response.data;
        return resdata[0].user;
      });
      return res;
    },
    updateCheckpoint: async function(id, name, location, user, score) {
      let token = this.$q.cookies.get("token");

      let apiurl =
        this.$api +
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

      await this.$axios.get(apiurl).then(response => {
        let resdata = response.data;
      });
    },
    updateUser: async function(id, name, password) {
      let token = this.$q.cookies.get("token");

      let apiurl =
        this.$api +
        "user/" +
        id +
        "/update/" +
        token +
        "/" +
        name +
        "/" +
        password;
      await this.$axios.get(apiurl).then(response => {
        let resdata = response.data;
      });
    },
    deleteUser: async function(id) {
      let token = this.$q.cookies.get("token");
      let apiurl = this.$api + "user/" + id + "/delete/" + token;

      await this.$axios.get(apiurl).then(response => {
        let resdata = response.data;
      });
    },
    deleteCheckpoint: async function(checkpoint) {
      let token = this.$q.cookies.get("token");

      let apiurl =
        this.$api + "checkpoint/" + checkpoint.id + "/delete/" + token;

      await this.$axios.get(apiurl).then(response => {
        let resdata = response.data;
      });

      await this.deleteUser(checkpoint.user);
      this.getCheckpoints();
    }
  }
};
</script>

<style scoped>
table {
  width: 100%;
}
</style>
