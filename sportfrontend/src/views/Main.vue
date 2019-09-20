<template>
  <div class="main">
    <h1>Klara-Oppenheimer-Schule</h1>
    <div>
      <div>
        <h2>Klaraktiv</h2>
        <h3>Allgemeine Informationen</h3>
        <p>Hallo lieber Schüler,</p>
        <p>hier auf dem Klaraktiv-Portal kannst du dir aktuelle Statistiken des Events ansehen. Aktuell befindest du dich auf der Startseite, hier werdeb Statistiken zu den Top 3 Klassen und Schülern angezeigt. Sollte dich interessieren, wie viele Punkte du erreicht hast, oder wie du im Vergleich zu anderen stehst, dann gib bitte deine Nummer in das nachfolgende Feld ein.</p>
        <p>Viel Spaß und viel Erfolg</p>
      </div>
      <div>
        <h2>Statistiken</h2>TODO: Input in Komponente auslagern (auch auf unterseite gebraucht)
        <input
          type="number"
          name
          id
          placeholder="Schülernummer"
        />
      </div>
    </div>TODO: Bibliothek für Statistiken einbinden.
    TODO: API für Statistikabfrage schreiben.
    <div>Statistik #1</div>
    <div>Statistik #2</div>
  </div>
</template>

<script>
export default {
  name: "main"
};
</script>
