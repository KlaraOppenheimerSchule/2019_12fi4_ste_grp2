<template>
  <div class="chart">
    <div class="text-h5">{{msg}}</div>
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
    updateChart: function(labels, vals) {
      let chart = this.chart;
      chart.data.labels = labels;
      chart.data.datasets.forEach(dataset => {
        dataset.data = vals;
      });
      chart.update();
    },
    getApiData: function() {
      let res = "";

      if (this.endpoint != "") {
        let apiurl = this.$api + this.endpoint;
        this.$axios.get(apiurl).then(response => {
          let resdata = response.data;

          let labels = [];
          let vals = [];
          for (let i = 0; i < resdata.length; i++) {
            if (resdata[i].name != undefined) {
              labels.push(resdata[i].name);
            } else {
              labels.push(resdata[i].id);
            }
            vals.push(resdata[i].score);
          }
          this.updateChart(labels, vals);
        });
      }
      return res;
    }
  },
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
    this.getApiData();
    this.chart = res;
  }
};
</script>

<style scoped lang="scss">
.chart {
  max-width: 299px;
  margin-left: auto;
  margin-right: auto;
}
</style>
