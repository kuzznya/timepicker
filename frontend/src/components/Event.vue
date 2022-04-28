<template>
  <b-card>
    <b-row>
      <b-col @click="$router.push(`/events/${id}`)">
        <h4>Go to the bar</h4>
      </b-col>
      <compact-date-picker v-model="dateRange"/>
<!--      <b-col>-->
<!--        <b-datepicker :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }" value="2002-03-10"/>-->
<!--      </b-col>-->
<!--      <b-col>-->
<!--        <b-datepicker :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }" value="2002-03-10"/>-->
<!--      </b-col>-->
      <b-col cols="1" align-self="center">
        <b-icon-trash font-scale="1.2" @click="deleteEvent"/>
      </b-col>
    </b-row>
  </b-card>
</template>

<script>
import CompactDatePicker from "@/components/CompactDatePicker";
export default {
  name: "Event",
  components: {CompactDatePicker},

  props: {
    id: null
  },

  methods: {
    async deleteEvent() {
      // FIXME implement
      const confirmed = await this.$bvModal.msgBoxConfirm("Are sure want to delete the event?")
      if (!confirmed)
        return
      // IMPLEMENT
      this.$emit('event-deleted', this.id)
    }
  }
}
</script>
