<template>
  <b-container>
    <b-card>
      <template #header>
        <b-icon-person-circle font-scale="5"/>
      </template>

      <b-card-text>Username: {{ username }}</b-card-text>
      <b-card-text>Email: {{ email }}</b-card-text>
    </b-card>
  </b-container>
</template>

<script>
export default {
  name: "Profile",

  props: {
    username: String
  },

  data: () => ({
    email: ""
  }),

  async mounted() {
    const profile = await this.$keycloak.loadUserProfile()
    if (this.username === profile.username)
      this.email = profile.email
  },

  async beforeUpdate() {
    const profile = await this.$keycloak.loadUserProfile()
    if (this.username === profile.username)
      this.email = profile.email
  }
}
</script>

<style scoped>

</style>
