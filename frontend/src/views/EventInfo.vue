<template>
  <b-container>
    <b-card>
      <h2>{{ title }}</h2>

      <p class="font-weight-bold">Author: @{{ author }}</p>

      <b-row>
        <b-col>
          <span>
            <compact-date-picker v-model="dateRange"/>
          </span>
        </b-col>
      </b-row>

      <v-calendar :attributes="attributes" @dayclick="onDayVoteClick" class="mt-3"/>

      <div v-if="!voted" class="mt-1">
        <h4 class="text-info">Please vote!</h4>
      </div>

      <div v-else class="mt-3">
        <h4>Results</h4>

        <b-row>
          <Bar :chart-data="chartData"
               :chart-options="chartOptions"
               :styles="{position: 'relative', width: '100%', height: '200px'}"
          />
        </b-row>

        <b-row>
          <b-col>
            <p class="lead">The best day for the event is<br/>
              <b class="font-weight-bold" @click="showParticipants(bestDate)">{{ bestDate }}</b>
            </p>
          </b-col>
        </b-row>
      </div>
    </b-card>

    <b-modal id="participants-modal" :ok-disabled="true" :cancel-disabled="true" :hide-footer="true">
      <b-card-title>Participants on {{ selectedResultDate }}</b-card-title>
      <b-card-body>
        <b-link v-for="participant in participants" :key="participant.username" :to="`/users/${participant.username}`">
          <b-row>
            {{ participant.name }} {{ participant.surname }} (@{{ participant.username }})
          </b-row>
        </b-link>
      </b-card-body>
    </b-modal>
  </b-container>
</template>

<script>
import { Bar } from 'vue-chartjs/legacy'
import { Chart as ChartJS, Title, Tooltip, BarElement, CategoryScale, LinearScale } from 'chart.js'
import CompactDatePicker from "@/components/CompactDatePicker";

ChartJS.register(Title, Tooltip, BarElement, CategoryScale, LinearScale)

export default {
  name: "EventInfo",

  components: {
    CompactDatePicker,
    Bar
  },

  props: {
    id: String
  },

  data: () => ({
    ws: null,

    title: "",
    author: "",

    dateRange: {
      start: new Date(),
      end: new Date()
    },

    voted: false,

    days: [],

    chartOptions: {
      responsive: true,
      maintainAspectRatio: false
    },
    chartData: {
      labels: [ '08.03.2002', '09.03.2002', '10.03.2002', '11.03.2002' ],
      datasets: [ { data: [8, 3, 15, 6] } ]
    },

    bestDate: null,

    selectedResultDate: String,
    participants: [
      {
        name: 'Ilya',
        surname: 'Kuznetsov',
        username: 'kuzznya'
      },
      {
        name: 'Max',
        surname: 'Golish',
        username: 'afterbvrner'
      },
      {
        name: 'Gamzat',
        surname: 'Gadgimagomedov',
        username: 'xaghoul'
      }
    ]
  }),

  created() {
    this.chartOptions.onClick = (event, item) => this.onChartClick(event, item)
  },

  async mounted() {
    await this.loadEventInfo()
    await this.loadSelectedDates()
    await this.loadStatistics()
    await this.setupWebSocket()
  },

  computed: {
    dates() {
      return this.days.map(day => day.date);
    },
    attributes() {
      return this.dates.map(date => ({
        highlight: true,
        dates: date,
      }));
    },
  },

  methods: {
    async loadEventInfo() {
      const event = await this.$axios.get(`http://localhost:4100/events/${this.id}`).then(response => response.data)
      this.title = event.title
      this.author = event.author
    },

    async loadSelectedDates() {
      const votes = await this.$axios.get(`http://localhost:4200/votes/${this.id}`).then(response => response.data)
      this.days = votes.map(vote => ({id: vote.date, date: Date.parse(vote.date) }))
      this.voted = this.days.length > 0
    },

    async loadStatistics() {
      const data = await this.$axios.get(`http://localhost:4200/votes/${this.id}/stats`).then(response => response.data)
      this.processStatistics(data.statistics)
    },

    processStatistics(stats) {
      if (stats == null)
        stats = {}
      const sorted = Object.entries(stats).sort((v1, v2) => v1[0].localeCompare(v2[0]))
      this.chartData = {
        labels: sorted.map(v => v[0]),
        datasets: [
          {
            data: sorted.map(v => v[1])
          }
        ]
      }
      this.bestDate = Object.keys(stats).sort(date => stats[date])[0]
    },

    async setupWebSocket() {
      const sessionId = await this.$axios.post("http://localhost:4000/sessions").then(result => result.data)
      this.ws = new WebSocket(`ws://localhost:4000/ws/${sessionId}/${this.id}`)
      this.ws.onmessage = event => {
        const stats = JSON.parse(event.data).statistics
        console.log(stats)

        this.processStatistics(stats)
        // const sorted = Object.entries(stats).sort((v1, v2) => v1[0].localeCompare(v2[0]))
        // this.chartData = {
        //   labels: sorted.map(v => v[0]),
        //   datasets: [
        //     {
        //       data: sorted.map(v => v[1])
        //     }
        //   ]
        // }
        // this.bestDate = Object.keys(stats).sort(date => stats[date])[0]

        console.log(this.days)
        console.log(this.dates)
        this.loadSelectedDates()
      }
      this.ws.onclose = function () {
        this.setupWebSocket()
      }
    },

    async send(date, state) {
      if (this.ws == null || this.ws.readyState === WebSocket.CLOSED) await this.setupWebSocket()
      this.ws.send(JSON.stringify({event: this.id, date: date, state: state}))
    },

    onDayVoteClick(day) {
      const idx = this.days.findIndex(d => d.id === day.id);
      console.log(day.id)
      if (idx >= 0) {
        this.days.splice(idx, 1);
        this.send(day.id, 'UNVOTED')
      } else {
        this.days.push({
          id: day.id,
          date: day.date,
        });
        this.send(day.id, 'VOTED')
      }
      this.voted = this.days.length > 0
    },

    showParticipants(date) {
      this.selectedResultDate = date
      this.$bvModal.show('participants-modal')
    },

    onChartClick(event, item) {
      this.showParticipants(this.chartData.labels[item[0].index])
    }
  }
}
</script>

<style scoped>

</style>
