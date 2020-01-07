<template>
  <ion-content class="main">
    <ion-grid>
      <ion-row>
        <ion-col>
          <ion-text color="primary">
            <h2>Statistiken für Schüler</h2>
          </ion-text>
        </ion-col>
      </ion-row>
      <ion-row>
        <ion-col>
          <BarChart
            propid="statsstudents"
            :endpoint="'stats/student/' + id"
            :msg="'Position von Schüler ' + id"
          ></BarChart>
        </ion-col>
        <ion-col>
          <BarChart
            propid="statsclasses"
            :endpoint="'stats/class/' + id"
            :msg="'Position der Klasse ' + classString"
          ></BarChart>
        </ion-col>
      </ion-row>
    </ion-grid>
  </ion-content>
</template>

<script>
import BarChart from "@/components/BarChart.vue";
import axios from "axios";

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
      let apiurl = process.env.VUE_APP_API_URL + "class/" + vm.id;

      axios.get(apiurl).then(response => {
        let resdata = response.data;

        console.log(resdata);
        vm.classString = resdata[0].name.toString();
      });
    }
  }
};
</script>
