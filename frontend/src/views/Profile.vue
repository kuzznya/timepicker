<template>
  <b-container>
    <b-card>
      <template #header>
        <b-icon-person-circle font-scale="5"/>
      </template>

      <b-card-text>Username: {{ profile.username }}</b-card-text>
      <b-card-text>Email: {{ profile.email }}</b-card-text>
    </b-card>
  </b-container>
</template>

<script>
export default {
  name: "Profile",

  data: () => ({
    profile: {
      email: "",
      username: ""
    }
  }),

  async mounted() {
    this.profile = await this.$keycloak.loadUserProfile()
  },

  async beforeUpdate() {
    this.profile = await this.$keycloak.loadUserProfile()
  }
}
</script>

<style scoped>

</style>
