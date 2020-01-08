<template>
  <ion-header>
    <ion-toolbar color="primary">
      <ion-buttons slot="start">
        <ion-back-button></ion-back-button>
      </ion-buttons>
      <ion-title>Klaraktiv</ion-title>
      <ion-buttons slot="end">
        <ion-button v-on:click="loginOut()">
          {{ title }}
        </ion-button>
      </ion-buttons>
    </ion-toolbar>
  </ion-header>
</template>

<script>
export default {
  name: "ionheader",
  computed: {
    isLoggedIn: function() {
      let res = this.$store.state.loggedIn;
      return res;
    },
    title: function(){
      if(this.$store.state.loggedIn){
        return "Logout";
      }else{
        return "Login";
      }
    }
  },
  methods: {
    loginOut: function(){
      if(this.$store.state.loggedIn){
        this.$cookie.delete('token');
        this.$cookie.delete('type');
        this.$store.commit('logOut');
      }else{
        this.$router.push({ path: "login" });
      }

    }
  }
};
</script>
