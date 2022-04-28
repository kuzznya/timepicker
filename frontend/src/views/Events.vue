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
      await this.$axios.post("http://localhost:4100/events", { title: title })
        .then(response => response.data)
      await this.updateEvents()
    },

    async updateEvents() {
      this.events = await this.$axios.get("http://localhost:4100/events").then(response => response.data)
    },

    async onEventDeleted() {
      await this.updateEvents()
    }
  }
}
</script>

<style scoped>

</style>
