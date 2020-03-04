<template>
  <q-page class="q-gutter-md column q-pa-md">
    <div class="text-h4 text-primary">Klassen bearbeiten</div>
    <q-list>
      <q-item v-for="item in classlist" :key="item.id">
        <q-item-section>
          <div class="text-body1">{{item.name}}</div>
        </q-item-section>
        <q-item-section>
          <div class="text-body1">{{item.students}}</div>
        </q-item-section>
        <q-item-section>
          <div class="row">
            <q-input type="number" value="1" v-model="item.addStudent" />
            <q-btn v-on:click="addStudent(item.id, item.addStudent)">Schüler hinzufügen</q-btn>
          </div>
        </q-item-section>
        <q-item-section>
          <router-link
            :to="{ path: '/admin/print/' + item.id }"
            target="_blank"
            class="classlistprint"
          >Klasse drucken</router-link>
        </q-item-section>
      </q-item>
    </q-list>
  </q-page>
</template>

<script>
export default {
  name: "addclass",
  data: function() {
    return {
      classlist: []
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
          this.refresh();
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
</style>
