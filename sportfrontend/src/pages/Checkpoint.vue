<template>
  <q-page class="column q-gutter-md q-pa-md">
    <div class="text-h4 text-primary">Station</div>
    <div class="row items-stretch">
      <q-input
        outlined
        v-model="studentid"
        type="number"
        label="Schülernummer eingeben"
        class="col-7"
      ></q-input>
      <q-space />
      <q-btn outline class="text-primary col-4 self-stretch" @click="eintragen()">Eintragen</q-btn>
    </div>
  </q-page>
</template>

<script>
export default {
  name: "admin",

  data() {
    return {
      studentid: "",
      error: false,
      success: false
    };
  },
  methods: {
    validateInput: function() {
      let validateVar = false;
      if (this.studentid.toString().length == 6) {
        validateVar = true;
      }
      return validateVar;
    },
    eintragen: function() {
      let token = this.$q.cookies.get("token");

      if (this.validateInput()) {
        let apiurl =
          this.$api +
          "checkpoint/" +
          this.id +
          "/visit/" +
          token +
          "/" +
          this.studentid;
        this.$axios.get(apiurl).then(response => {
          let resdata = response.data;
          if (resdata[0].error != undefined) {
            this.errortext = resdata[0].error;
            this.error = true;
          } else {
            this.error = false;
            this.success = true;
          }
        });
      } else {
        this.errortext = "Schülernummer ist nicht gültig";
        this.error = true;
      }
    }
  },
  computed: {
    id: function() {
      if (this.$route != undefined) {
        return this.$route.params.id;
      }
      return 0;
    }
  },
  mounted: function() {
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
  }
};
</script>
