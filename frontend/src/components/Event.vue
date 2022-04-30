<template>
  <b-card>
    <b-row class="justify-content-center">
      <h4 @click="$router.push(`/events/${id}`)" class="mx-3 w-50 btn-link">
        {{ title }}
      </h4>

      <b-form-row class="mx-5 align-self-center">
        <compact-date-picker v-model="dateRange"/>
      </b-form-row>

      <b-icon-trash @click="deleteEvent"
                    font-scale="1.2"
                    class="position-absolute align-self-center"
                    style="right: 10px;"
      />
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
