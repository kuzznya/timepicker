<template>
  <v-date-picker v-model="range" is-range>
    <template v-slot="{ inputValue, inputEvents }">
      <b-button-group class="flex justify-center items-center">
        <input
          :value="inputValue.start"
          v-on="inputEvents.start"
          style="width: 12ch;"
          class="border px-2 py-1 rounded-left focus:outline-none focus:border-indigo-300"
        />

        <b-button :disabled="true" variant="light">
          <b-icon-arrow-right/>
        </b-button>

        <input
          :value="inputValue.end"
          v-on="inputEvents.end"
          style="width: 12ch;"
          class="border px-2 py-1 rounded-right focus:outline-none focus:border-indigo-300"
        />
      </b-button-group>
    </template>
  </v-date-picker>
</template>

<script>
export default {
  name: "CompactDatePicker",

  model: {
    prop: 'value',
    event: 'input'
  },

  props: {
    value: {
      start: Date,
      end: Date
    },
    confirmBeforeUpdate: {
      type: Boolean,
      default: false
    },
    updateCallback: {
      type: Function,
      default: () => {}
    }
  },

  computed: {
    changed() {
      if (this.value.start == null || this.value.end == null)
        return false
      return this.range.start.getTime() !== this.value.start.getTime() ||
        this.range.end.getTime() !== this.value.end.getTime()
    }
  },

  data: () => ({
    range: {
      start: null,
      end: null
    }
  }),

  mounted() {
    this.range = this.value
  },

  methods: {
    async updateWithConfirmation(value) {
      const answer = await this.$bvModal.msgBoxConfirm(
        "Are you sure want to change the dates? Votes for some dates can be lost"
      )
      if (answer) {
        try {
          await this.updateCallback(value)
          this.$emit('input', value)
        } catch {
          this.$bvToast.toast('Cannot update dates, please try again later',
            { variant: 'danger', toaster: 'b-toaster-bottom-right' })
          this.range = this.value
        }
      }
      else {
        this.range = this.value
      }
    },

    async updateWithoutConfirmation(value) {
      try {
        await this.updateCallback(value)
        this.$emit('input', value)
      } catch {
        this.$bvToast.toast('Cannot update dates, please try again later',
          { variant: 'danger', toaster: 'b-toaster-bottom-right' })
        this.range = this.value
      }
    }
  },

  watch: {
    async range(value) {
      if (!this.changed)
        return
      if (this.confirmBeforeUpdate) await this.updateWithConfirmation(value)
      else await this.updateWithoutConfirmation(value)
    },

    value() {
      if (this.range !== this.value) this.range = this.value
    }
  }
}
</script>
