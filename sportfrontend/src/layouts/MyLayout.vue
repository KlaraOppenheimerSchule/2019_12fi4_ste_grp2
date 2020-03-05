<template>
  <q-layout view="hHh lpR fFf">
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-toolbar-title>
          <q-avatar rounded class="bg-white" elevated>
            <img src="~assets/logo-klara.png" />
          </q-avatar>&nbsp;KlarAktiv
        </q-toolbar-title>

        <q-btn dense flat round icon="menu" @click="right = !right" />
      </q-toolbar>
    </q-header>

    <q-drawer v-model="right" side="right" overlay elevated>
      <!-- drawer content -->
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
export default {
  data() {
    return {
      right: false
    };
  }
};
</script>

<style lang="scss">
.q-page{
  max-width: 900px;
  margin-left: auto;
  margin-right: auto;
}
</style>