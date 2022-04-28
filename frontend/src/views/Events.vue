<template>
  <b-container>
    <h2>Events</h2>
    <Event v-for="e in events"
           :key="e.id"
           :id="e.id"
           :title="e.title"
           @event-deleted="onEventDeleted"
           class="mb-3"
    />
    <b-button variant="light" @click="$bvModal.show('event-modal')">
      <b-icon-plus/>
      Add
    </b-button>

    <b-modal id="event-modal" @ok="addEvent(title)">
      <label for="title-input">Title:</label>
      <b-form-input id="title-input" v-model="title"/>
    </b-modal>
  </b-container>
</template>

<script>
import Event from "@/components/Event";
import eventsApi from "@/api/events"

export default {
  name: "Events",
  components: {Event},
  data: () => ({
    events: [],
    title: ""
  }),

  async mounted() {
    await this.updateEvents()
  },

  methods: {
    async addEvent(title) {
      await eventsApi.addEvent({title: title})
      await this.updateEvents()
    },

    async updateEvents() {
      this.events = await eventsApi.getEvents()
    },

    async onEventDeleted() {
      await this.updateEvents()
    }
  }
}
</script>

<style scoped>

</style>
