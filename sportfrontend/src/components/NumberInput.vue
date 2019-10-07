<template>
  <ion-grid>
    <ion-row>
      <ion-col>
        <ion-item>
          <ion-label position="floating">Schülernummer</ion-label>
          <ion-input
            type="tel"
            :value="val"
            @input="inputevent($event.target)"
            maxlength="6"
          ></ion-input>
        </ion-item>
        <ion-text v-if="error" color="danger">Schülernummer nicht gültig</ion-text>
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
      val: null,
      error: false
    };
  },
  methods: {
      inputevent: function(target){
          this.val = target.value;
      },
      validateInput: function(){
        let validateVar = false;
          if(this.val.length == 6){
              validateVar = true
          }
          return validateVar;
      },
      routeToId: function() {
        if(this.validateInput()){
            this.$router.push({ path: "stats/" + this.val });
        }else{
            this.error = true;
        }
    }
  }
};
</script>

<style></style>
