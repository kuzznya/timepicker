<template>
  <b-card>
    <b-row>
      <b-col @click="$router.push(`/events/${id}`)">
        <h4>{{ title }}</h4>
      </b-col>
      <compact-date-picker v-model="dateRange"/>
      <b-col cols="1" align-self="center">
        <b-icon-trash font-scale="1.2" @click="deleteEvent"/>
      </b-col>
    </b-row>
  </b-card>
</template>

<script>
import CompactDatePicker from "@/components/CompactDatePicker";
import events from "@/api/events"

export default {
  name: "Event",
  components: {CompactDatePicker},

  props: {
    id: null,
    title: String
  },

  data: () => ({
    dateRange: {
      start: new Date(),
      end: new Date()
    }
  }),

  methods: {
    async deleteEvent() {
      const confirmed = await this.$bvModal.msgBoxConfirm("Are sure want to delete the event?")
      if (!confirmed)
        return
      await events.deleteEvent()
      this.$emit('event-deleted')
    }
  }
}
</script>
