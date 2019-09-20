<template>
  <ion-grid>
    <ion-row>
      <ion-col>
        <ion-item>
          <ion-label position="floating">Schülernummer</ion-label>
          <ion-input
            type="number"
            :value="val"
            @input="val = $event.target.value"
          ></ion-input>
        </ion-item>
        <ion-button size="full" v-on:click="routeToId()">Button</ion-button>
      </ion-col>
    </ion-row>
  </ion-grid>
</template>

<script>
export default {
  name: "numberinput",
  data: function() {
    return {
      val: null
    };
  },
  methods: {
    routeToId: function() {
      this.$router.push({ path: "stats/" + this.val });
    }
  }
};
</script>

<style></style>
