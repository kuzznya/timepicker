<template>
  <b-container>
    <b-card>
      <h2>{{ title }}</h2>

      <p class="font-weight-bold">
        Author: <router-link class="text-dark" :to="'/users/' + author">@{{ author }}</router-link>
      </p>

      <b-row>
        <b-col>
          <span>
            <compact-date-picker v-model="dateRange" :update-callback="updateDates" :confirm-before-update="true"/>
          </span>
        </b-col>
      </b-row>

      <v-calendar @dayclick="onDayVoteClick"
                  :attributes="attributes"
                  :min-date="dateRange.start"
                  :max-date="dateRange.end"
                  class="mt-3"
      />

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

        <b-row v-if="bestDates.length > 0">
          <b-col>
            <p class="lead">{{ bestDates.length === 1 ? "The best day for the event is" : "The best days for the event are"}}<br/>
              <span v-for="(date, idx) in bestDates" :key="date">
                <b-link class="font-weight-bold" @click="showParticipants(date)">{{ date }}</b-link>
                <span v-if="idx < bestDates.length - 1">, </span>
              </span>
            </p>
          </b-col>
        </b-row>
      </div>
    </b-card>

    <b-modal id="participants-modal" :ok-disabled="true" :cancel-disabled="true" :hide-footer="true">
      <b-card-title>Participants on {{ selectedResultDate }}</b-card-title>
      <b-card-body>
        <b-link v-for="username in participants" :key="username" :to="`/users/${username}`" class="text-dark">
          <b-row>
             @{{ username }}
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
import ws from "@/api/ws";
import events from "@/api/events";
import votes from "@/api/votes";

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
    messages: [],

    title: "",
    author: "",

    dateRange: {
      start: null,
      end: null
    },

    days: [],

    chartOptions: {
      responsive: true,
      maintainAspectRatio: false
    },
    chartData: {
      labels: [ '08.03.2002', '09.03.2002', '10.03.2002', '11.03.2002' ],
      datasets: [ { data: [8, 3, 15, 6] } ]
    },

    bestDates: [],

    selectedResultDate: String,
    participants: []
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

    voted() {
      return this.days.length > 0
    }
  },

  methods: {
    async loadEventInfo() {
      const event = await events.getEventInfo(this.id)
      this.title = event.title
      this.author = event.author
      this.dateRange = {
        start: new Date(event.minDate),
        end: new Date(event.maxDate)
      }
    },

    async loadSelectedDates() {
      const userVotes = await votes.getUserVotes(this.id)
      this.days = userVotes.map(vote => ({ id: vote.date, date: Date.parse(vote.date) }))
    },

    async loadStatistics() {
      const data = await votes.getStatistics(this.id)
      this.processStatistics(data.statistics)
    },

    processStatistics(stats) {
      if (stats == null) {
        stats = {}
        this.bestDates = []
        return
      }
      const sorted = Object.entries(stats).sort((v1, v2) => v1[0].localeCompare(v2[0]))
      this.chartData = {
        labels: sorted.map(v => v[0]),
        datasets: [
          {
            data: sorted.map(v => v[1])
          }
        ]
      }
      const maxVotes = Object.values(stats)
        .sort((v1, v2) => v1 - v2)
        .reverse()[0]
      this.bestDates = Object.keys(stats)
        .sort()
        .filter(date => stats[date] === maxVotes)
    },

    processSelectedDates(dates) {
      if (dates == null) {
        this.days = []
        return
      }
      this.days = dates.map(date => ({id: date, date: Date.parse(date) }))
    },

    async setupWebSocket() {
      this.ws = await ws.setupWebSocket(this.id)

      this.ws.onopen = () => {
        while (this.messages.length > 0) this.ws.send(this.messages.pop())
      }

      this.ws.onmessage = event => {
        const data = JSON.parse(event.data)
        switch (data.type) {
          case "VOTES": {
            const votes = data.votes
            this.processSelectedDates(votes)
            break;
          }
          case "STATISTICS": {
            const stats = data.statistics
            this.processStatistics(stats)
            break;
          }
        }
      }

      this.ws.onclose = () => {
        this.setupWebSocket()
      }
    },

    async send(date, state) {
      const message = JSON.stringify({event: this.id, date: date, state: state})
      if (this.ws.readyState === WebSocket.OPEN) {
        this.ws.send(message)
      } else {
        this.messages.push(message)
        await this.setupWebSocket()
      }
    },

    onDayVoteClick(day) {
      const idx = this.days.findIndex(d => d.id === day.id)
      if (idx >= 0) {
        this.send(day.id, 'UNVOTED')
      } else {
        this.send(day.id, 'VOTED')
      }
    },

    async updateDates(range) {
      await events.changeDates(this.id, range.start, range.end)
    },

    async showParticipants(date) {
      this.selectedResultDate = date
      const allVotes = await votes.getAllVotes(this.id)
      this.participants = allVotes
        .filter(vote => vote.date === date)
        .map(vote => vote.username)
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
