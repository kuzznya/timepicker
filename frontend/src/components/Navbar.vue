<template>
  <b-navbar type="dark" variant="dark">
    <b-navbar-brand to="/" class="font-weight-bold ml-3">
      Timepicker
    </b-navbar-brand>

    <b-navbar-nav>
      <b-nav-item to="/">Home</b-nav-item>
      <b-nav-item to="/events" v-if="authenticated">Events</b-nav-item>
    </b-navbar-nav>

    <b-navbar-nav class="ml-auto">
      <b-nav-item-dropdown right no-caret v-if="authenticated">
        <template #button-content>
          <b-icon-person/>
        </template>

        <b-dropdown-item :to="profileLink">
          <b-icon-person-badge/>
          Profile
        </b-dropdown-item>
        <b-dropdown-item @click="logout">
          <b-icon-box-arrow-in-right/>
          Log out
        </b-dropdown-item>
      </b-nav-item-dropdown>
    </b-navbar-nav>
  </b-navbar>
</template>

<script>
export default {
  name: "Navbar",

  computed: {
    authenticated() {
      return this.$keycloak.authenticated
    },

    profileLink() {
      return '/users/' + this.$keycloak.userName ?? ''
    }
  },

  methods: {
    logout() {
      console.log(this.$keycloak)
      this.$keycloak.logoutFn()
    }
  }
}
</script>

<style scoped>

</style>
