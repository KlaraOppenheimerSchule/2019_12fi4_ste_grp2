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
        Statistik #1
      </div>
    </div>
  </ion-content>
</template>

<script>
export default {
  name: "statsid",
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
