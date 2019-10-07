<template>
    <div class="chart">
        <canvas :id="propid" width="300" height="300"></canvas>
    </div>
</template>

<script>
import Chart from "chart.js";
export default {
  props: ["propid"],
  data() {
    return {
      chart: null
    };
  },
  mounted() {
    let res = new Chart(this.propid, {
      type: "bar",
      data: {
        labels: ["548934", "871544", "120563"],
        datasets: [
          {
            label: "Punkte",
            data: [11, 12, 10],
            backgroundColor: [
              "rgba(255, 99, 132, 0.2)",
              "rgba(54, 162, 235, 0.2)",
              "rgba(255, 206, 86, 0.2)"
            ]
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

<style scoped>
</style>