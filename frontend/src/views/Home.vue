<template>
  <b-container class="home">
    <b-row class="mt-5">
      <b-col cols="3">
        <b-icon-people font-scale="10"/>
      </b-col>
    </b-row>

    <b-row>
      <b-col>
        <p class="lead text-justify">
          Do you want to decide when to go to the bar with your friends but all of you are VEEEERY busy
          so you cannot decide when to go and just staying angry at each other?<br/>
          Timepicker is created for you! Just create an event, set date restrictions and send the link to your friends.
          They will have to set the dates that are convenient for them, and after everyone is finished,
          our system can provide the best day when the majority of people is available.
          <b v-if="!authenticated" class="font-weight-bold">Try it now!</b>
        </p>
      </b-col>
    </b-row>

    <b-row>
      <b-col v-if="authenticated">
        <b-button to="/events">See my events</b-button>
      </b-col>
      <b-col v-else>
        <b-row>
          <b-col>
            <b-button @click="authenticate" variant="dark">Authenticate</b-button>
            <p>Please authenticate to continue</p>
          </b-col>
        </b-row>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
export default {
  name: 'Home',

  data: () => ({
  }),

  computed: {
    authenticated() {
      return this.$keycloak.authenticated
    }
  },

  methods: {
    authenticate() {
      this.$keycloak.login()
    }
  }
}
</script>
