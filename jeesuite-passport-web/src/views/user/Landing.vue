<template>
</template>

<script lang="ts">
  import { defineComponent } from 'vue';
  import { useUserStore } from '/@/store/modules/user';
  import { useRoute } from 'vue-router'
  
  export default defineComponent({
    name: 'Landing',
    components: {},
    setup() {
      const userStore = useUserStore();
      userStore.checkLoginStatus();
  
      return {};
    },
  });
</script>

<style>
</style>
