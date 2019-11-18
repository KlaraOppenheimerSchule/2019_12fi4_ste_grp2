<template>
  <ion-content class="main">
    <h1>Klara-Oppenheimer-Schule</h1>
    <div>
      <h2>Statistiken für Schüler</h2>
      TODO: Akkurate Statistiken implementieren
      <div>
        Aktuell auf Platz: NaN
      </div>

      <div>
        <ion-grid>
          <ion-row>
            <ion-col>
              <BarChart
                propid="statsstudents"
                :endpoint="'stats/student/' + id"
                :msg="'Position von Schüler ' + id"
              ></BarChart>
            </ion-col>
          </ion-row>
        </ion-grid>
      </div>
    </div>
  </ion-content>
</template>

<script>
import BarChart from "@/components/BarChart.vue";
export default {
  name: "statsid",
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
  }
};
</script>
