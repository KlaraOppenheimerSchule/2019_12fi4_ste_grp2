<template>
  <div class="chart">
    <h2>{{ msg }}</h2>
    <canvas :id="propid" width="300" height="300"></canvas>
  </div>
</template>

<script>
import Chart from "chart.js";
export default {
  props: ["propid", "msg", "endpoint"],
  data() {
    return {
      chart: null,
      data: []
    };
  },
  methods: {
    updateChart: function() {
      let chart = this.chart;
      //TODO: Update Chart based on API Data
      return chart;
    }
  },
  //TODO: Connect to API process.env.VUE_APP_API_URL + this.endpoint
  mounted() {
    let res = new Chart(this.propid, {
      type: "bar",
      data: {
        labels: ["", "", ""],
        datasets: [
          {
            label: "Punkte",
            data: [0, 0, 0],
            backgroundColor: [
              "rgba(255, 99, 132, 0.2)",
              "rgba(54, 162, 235, 0.2)",
              "rgba(255, 206, 86, 0.2)"
            ],
            borderColor: [
              "rgba(255, 99, 132, 1)",
              "rgba(54, 162, 235, 1)",
              "rgba(255, 206, 86, 1)"
            ],
            borderWidth: 1
          }
        ]
      },
      options: {
        scales: {
          yAxes: [
            {
              ticks: {
                beginAtZero: true
              }
            }
          ]
        }
      }
    });
    this.chart = res;
  }
};
</script>

<style scoped></style>
