<template>
  <ion-app id="app">
    <IonHeader></IonHeader>
    <router-view />
    <!-- TODO: Footer hinzufügen -->
  </ion-app>
</template>

<script>
import IonHeader from "./components/IonHeader.vue";

export default {
  components: {
    IonHeader
  }
};
</script>

<style lang="scss">
:root {
  --ion-color-primary: #ec880e;
}

ion-grid {
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

@media print {
  .printhide {
    display: none !important;
  }
  .ion-page {
    position: static !important;
    display: block !important;
    overflow: visible !important;
    contain: none !important;
  }
}
</style>
