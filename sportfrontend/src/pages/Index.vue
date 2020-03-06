<template>
  <q-page class="column q-gutter-md q-pa-md">
    <div class="text-h4 text-primary">Klara-Oppenheimer-Schule</div>
    <div class="column">
      <div class="text-h4">Allgemeine Informationen</div>
      <br />
      <div class="text-body1">
        Hallo lieber Schüler,
        <br />hier auf dem Klaraktiv-Portal kannst du dir aktuelle Statistiken des Events ansehen. Aktuell befindest du dich auf der Startseite, hier werden Statistiken zu den Top 3 Klassen, Top 3 Schülern angezeigt. Sollten dich deine eigenen Punkte interessieren und wie du im Vergleich zu anderen stehst, gib bitte deine Nummer in das nachfolgende Suchfeld ein.
        <br />Viel Spaß und viel Erfolg.
      </div>
    </div>
    <div class="column">
      <div class="text-h4 q-mb-md">Statistiken</div>
      <NumberInput></NumberInput>
    </div>
    <div class="column q-gutter-md">
      <BarChart propid="statsclasses" endpoint="stats/top/class/3" msg="Top 3 Klassen"></BarChart>
      <BarChart propid="statsstudents" endpoint="stats/top/student/3" msg="Top 3 Schüler"></BarChart>
    </div>
  </q-page>
</template>

<script>
import BarChart from "components/BarChart.vue";
import NumberInput from "components/NumberInput.vue";

export default {
  name: "home",
  components: {
    BarChart,
    NumberInput
  }
};
</script>
