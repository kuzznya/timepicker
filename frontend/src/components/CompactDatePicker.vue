<template>
  <v-date-picker v-model="range" is-range>
    <template v-slot="{ inputValue, inputEvents }">
      <div class="flex justify-center items-center">
        <input
          :value="inputValue.start"
          v-on="inputEvents.start"
          :readonly="true"
          size="10"
          class="border px-2 py-1 rounded focus:outline-none focus:border-indigo-300"
        />
        <b-icon-arrow-right class="mx-2"/>
        <input
          :value="inputValue.end"
          v-on="inputEvents.end"
          size="10"
          class="border px-2 py-1 rounded focus:outline-none focus:border-indigo-300"
        />
      </div>
    </template>
  </v-date-picker>
</template>

<script>
export default {
  name: "CompactDatePicker",

  prop: ['value'],

  data: () => ({
    range: {
      start: new Date(),
      end: new Date()
    }
  }),

  watch: {
    range: function (value) {
      this.$emit('input', value)
    }
  }
}
</script>
