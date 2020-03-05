<template>
  <q-page class="q-gutter-md q-pa-md column">
    <div class="text-h4 text-primary">Schüler austragen</div>
    <q-input label="Schülernummer" class="numberInput" type="tel" v-model="val" maxlength="6"></q-input>
    <div class="text-body1 text-negative" v-if="error">{{ errortext }}</div>
    <div class="text-body1 text-positive" v-if="success">Erfolgreich ausgetragen</div>
    <q-btn class="full-width" v-on:click="austragen()">Schüler austragen</q-btn>
  </q-page>
</template>

<script>
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
  },
  methods: {
    validateInput: function() {
      let validateVar = false;
      if (this.val.toString().length == 6) {
        validateVar = true;
      }
      return validateVar;
    },
    austragen: function() {
      let token = this.$q.cookies.get("token");

      if (this.validateInput()) {
        let apiurl =
          this.$api + "student/" + this.val + "/present/" + token + "/false";

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
  }
};
</script>

<style lang="scss" scoped></style>
