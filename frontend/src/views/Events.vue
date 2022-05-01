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

    <b-modal id="event-modal" @ok="addEvent" title="New event">
      <label for="title-input">Title:</label>
      <b-form-input id="title-input" v-model="newEvent.title" class="mb-3"/>
      <label for="min-date-input">Dates:</label>
      <compact-date-picker v-model="newEvent.dateRange"/>
    </b-modal>
  </b-container>
</template>

<script>
import Event from "@/components/Event";
import CompactDatePicker from "@/components/CompactDatePicker";
import events from "@/api/events"

export default {
  name: "Events",
  components: {CompactDatePicker, Event},
  data: () => ({
    events: [],
    newEvent: {
      title: "",
      dateRange: {
        start: new Date(),
        end: new Date()
      }
    }
  }),

  async mounted() {
    await this.updateEvents()
  },

  methods: {
    async addEvent() {
      const event = {
        title: this.newEvent.title,
        minDate: this.newEvent.dateRange.start,
        maxDate: this.newEvent.dateRange.end
      }
      await events.addEvent(event)
      await this.updateEvents()
      this.newEvent.title = ""
    },

    async updateEvents() {
      this.events = await events.getEvents()
    },

    async onEventDeleted() {
      await this.updateEvents()
    }
  }
}
</script>

<style scoped>

</style>
