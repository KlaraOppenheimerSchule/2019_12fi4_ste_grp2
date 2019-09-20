<template>
  <ion-content class="main">
    <ion-grid>
      <ion-row>
        <ion-col>
          <ion-text color="primary">
            <h2>Klara-Oppenheimer-Schule</h2>
          </ion-text>
        </ion-col>
      </ion-row>
      <ion-row>
        <ion-col>
          <div>
            <h2>Allgemeine Informationen</h2>
            <p>Hallo lieber Schüler,</p>
            <p>
              hier auf dem Klaraktiv-Portal kannst du dir aktuelle Statistiken
              des Events ansehen. Aktuell befindest du dich auf der Startseite,
              hier werdeb Statistiken zu den Top 3 Klassen und Schülern
              angezeigt. Sollte dich interessieren, wie viele Punkte du erreicht
              hast, oder wie du im Vergleich zu anderen stehst, dann gib bitte
              deine Nummer in das nachfolgende Feld ein.
            </p>
            <p>Viel Spaß und viel Erfolg</p>
          </div>
        </ion-col>
      </ion-row>
      <ion-row>
        <ion-col>
          <h2>Statistiken</h2>
          <NumberInput></NumberInput>
        </ion-col>
      </ion-row>
    </ion-grid>
    <div></div>
    TODO: Bibliothek für Statistiken einbinden. TODO: API für Statistikabfrage
    schreiben.
    <div>Statistik #1</div>
    <div>Statistik #2</div>
  </ion-content>
</template>

<script>
import NumberInput from "@/components/NumberInput.vue";

export default {
  name: "home",
  components: {
    NumberInput
  }
};
</script>
