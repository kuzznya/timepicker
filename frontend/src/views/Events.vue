<template>
  <b-container>
    <h2>Events</h2>
    <Event v-for="e in events"
           :key="e.id"
           :id="e.id"
           @event-deleted="onEventDeleted"
           class="mb-3"
    />
    <b-button variant="light" @click="addEvent">
      <b-icon-plus/>
      Add
    </b-button>
  </b-container>
</template>

<script>
import Event from "@/components/Event";
export default {
  name: "Events",
  components: {Event},
  data: () => ({
    events: [
      {
        id: 1
      },
      {
        id: 2
      }
    ]
  }),

  methods: {
    addEvent() {
      // FIXME
      this.events.push(
        { id: this.events[this.events.length - 1].id + 1 }
      )
    },

    onEventDeleted(id) {
      this.events = this.events.filter(event => event.id !== id)
    }
  }
}
</script>

<style scoped>

</style>
