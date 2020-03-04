<template>
  <q-page class="column q-gutter-md q-pa-md">
    <div class="column">
      <div class="text-h4 text-primary">Statistiken für Schüler</div>
    </div>
    <div class="column">
      <BarChart
        propid="statsstudents"
        :endpoint="'stats/student/' + id"
        :msg="'Position von Schüler ' + id"
      ></BarChart>
      <BarChart
        propid="statsclasses"
        :endpoint="'stats/class/' + id"
        :msg="'Position der Klasse ' + classString"
      ></BarChart>
    </div>
  </q-page>
</template>

<script>
import BarChart from "components/BarChart.vue";

export default {
  name: "statsid",
  data() {
    return {
      classString: "xxXXx"
    };
  },
  components: {
    BarChart
  },
  computed: {
    id: function() {
      if (this.$route != undefined) {
        if (this.$route.params.id.toString().length == 6) {
          return this.$route.params.id;
        } else {
          return 0;
        }
      }
      return 0;
    }
  },
  mounted: function() {
    let vm = this;
    if (vm.id != 0) {
      let apiurl = this.$api + "student/" + vm.id + "/class";

      this.$axios.get(apiurl).then(response => {
        let resdata = response.data;

        vm.classString = resdata[0].name.toString();
      });
    }
  }
};
</script>
