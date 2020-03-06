<template>
  <q-page class="q-gutter-md column q-pa-md">
    <div class="text-h4 text-primary">Klassen bearbeiten</div>
    <q-list bordered>
      <template v-for="item in classlist">
        <div class="row" :key="item.id">
          <div class="col-2 q-pa-md">
            <div class="text-body1">{{item.name}}</div>
          </div>
          <div class="col-2 q-pa-md">
            <div class="text-body1">{{item.students}}</div>
          </div>
          <div class="col-6 q-pa-md">
            <div class="row no-wrap justify-evenly">
              <q-input class="col-5" outlined type="number" value="1" v-model="item.addStudent" />
              <q-btn
                class="col-5"
                v-on:click="addStudent(item.id, item.addStudent)"
                :disabled="item.addStudent <= 0"
              >Schüler hinzufügen</q-btn>
            </div>
          </div>
          <div class="col-2 q-pa-md">
            <q-btn
              :to="{ path: '/admin/print/' + item.id }"
              target="_blank"
              class="classlistprint"
              :disabled="item.students <= 0"
            >Klasse drucken</q-btn>
          </div>
        </div>
        <q-separator :key="item.id + '_separator'" v-if="item !== classlist[classlist.length-1]"></q-separator>
      </template>
    </q-list>
    <div class="row justify-center q-gutter-md">
      <q-input outlined v-model="newClassName" />
      <q-btn
        outline
        @click="addNewClass()"
        class="text-primary"
        :disabled="newClassName.length <= 2"
      >Neue Klasse hinzufügen</q-btn>
    </div>
  </q-page>
</template>

<script>
export default {
  name: "addclass",
  data: function() {
    return {
      classlist: [],
      newClassName: ""
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

    this.refresh();
  },
  methods: {
    refresh: function() {
      let token = this.$q.cookies.get("token");
      let classlistapiurl = this.$api + "class/all/" + token;
      this.$axios.get(classlistapiurl).then(response => {
        let resdata = response.data;
        resdata.forEach(element => {
          element.addStudent = 0;
        });
        this.classlist = resdata;
      });
    },
    addStudent: function(target, addStudent) {
      let token = this.$q.cookies.get("token");
      let amount = addStudent;

      let apiurl =
        this.$api + "student/create/" + token + "/" + target + "/" + amount;

      this.$axios.get(apiurl).then(response => {
        let resdata = response.data;
        if (resdata[0].students != undefined) {
          this.$q.notify({
            message: amount + " Schüler wurden erfolgreich hinzgefügt",
            color:"positive"
          });
          this.refresh();
        }
      });
    },
    addNewClass() {
      if (this.newClassName.length > 2) {
        let token = this.$q.cookies.get("token");
        let name = this.newClassName;

        let apiurl = this.$api + "class/create/" + token + "/" + name;

        this.$axios.get(apiurl).then(response => {
          let resdata = response.data;
          if (resdata[0].class != undefined) {
            this.$q.notify({
              message: "Klasse erfolgreich hinzugefügt",
              color: "positive"
            });
            this.refresh();
          } else {
            this.$q.notify({
              message: "Klasse konnte nicht hinzugefügt werden",
              color: "negative"
            });
          }
        });
      } else {
        this.$q.notify({
          message: "Name zu kurz",
          color: "negative"
        });
      }
    }
  }
};
</script>

<style lang="scss" scoped>
</style>
