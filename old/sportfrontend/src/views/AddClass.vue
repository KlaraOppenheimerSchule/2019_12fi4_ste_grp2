<template>
  <ion-content class="main">
    <ion-grid>
      <ion-row>
        <ion-col>
          <ion-text color="primary">
            <h2>Klassen bearbeiten</h2>
          </ion-text>
        </ion-col>
      </ion-row>
      <ion-row>
        <ion-col>
          <ion-list>
            <div class="classlistitem" v-for="item in classlist" :key="item.id">
              <span class="classlistlabel">{{ item.name }}</span>
              <span class="classlistcount">{{ item.students }}</span>
              <span class="classlistaddStudent">
                <input
                  type="number"
                  :name="'number' + item.id"
                  :id="'number' + item.id"
                  value="1"
                />
                <button v-on:click="addStudent(item.id)">
                  Schüler hinzufügen
                </button>
              </span>
              <router-link
                :to="{ name: 'print', params: { id: item.id } }"
                target="_blank"
                class="classlistprint"
                >Klasse drucken</router-link
              >
              <!--TODO: Implement this -->
            </div>
          </ion-list>
        </ion-col>
      </ion-row>
    </ion-grid>
  </ion-content>
</template>

<script>
import axios from "axios";
export default {
  name: "addclass",
  data: function() {
    return {
      classlist: []
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

    this.refresh();
  },
  methods: {
    refresh: function() {
      let token = this.$cookie.get("token");
      let classlistapiurl = process.env.VUE_APP_API_URL + "class/all/" + token;
      axios.get(classlistapiurl).then(response => {
        let resdata = response.data;
        this.classlist = resdata;
      });
    },
    addStudent: function(target) {
      let token = this.$cookie.get("token");
      let amount = 0;

      amount = document.getElementById("number" + target).value;

      let apiurl =
        process.env.VUE_APP_API_URL +
        "student/create/" +
        token +
        "/" +
        target +
        "/" +
        amount;

      axios.get(apiurl).then(response => {
        let resdata = response.data;
        if (resdata[0].students != undefined) {
          this.refresh();
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.classlistitem {
  display: grid;
  grid-template:
    "label . . count"
    "add add print print" / 1fr 1fr 1fr 1fr;
  gap: 20px;
  padding-bottom: 20px;
  padding-top: 20px;
  border-bottom: 1px solid black;
}

.classlistlabel {
  grid-area: label;
}
.classlistcount {
  grid-area: count;
  text-align: right;
}
.classlistaddStudent {
  grid-area: add;
}
.classlistprint {
  grid-area: print;
  text-align: right;
}
</style>
