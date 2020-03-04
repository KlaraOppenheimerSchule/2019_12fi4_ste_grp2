<template>
  <div class="column">
    <q-input
      outlined
      label="Schülernummer"
      class="numberInput"
      type="tel"
      v-model="val"
      maxlength="6"
    ></q-input>
    <div class="text-body2 full-width" v-if="error" color="danger">Schülernummer nicht gültig</div>
    <q-btn class="full-width bg-primary text-white q-mt-md" @click="routeToId()">Button</q-btn>
  </div>
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
    validateInput: function() {
      let validateVar = false;
      if (this.val.toString().length == 6) {
        validateVar = true;
      }
      return validateVar;
    },
    routeToId: function() {
      if (this.validateInput()) {
        this.$router.push({ path: "stats/" + this.val });
      } else {
        this.error = true;
      }
    }
  }
};
</script>