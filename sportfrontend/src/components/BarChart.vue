<template>
  <div class="chart">
    <h2>{{ msg }}</h2>
    <canvas :id="propid" width="300" height="300"></canvas>
  </div>
</template>

<script>
import Chart from "chart.js";
import axios from "axios";
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
        chart.data.datasets.forEach((dataset) => {
          dataset.data = vals;
        });
      //TODO: Update Chart based on API Data
      chart.update();
    },
    getApiData: function(){
      let res = "";

      if(this.endpoint != ""){

        let apiurl = process.env.VUE_APP_API_URL + this.endpoint;

        console.log(apiurl);

        axios.get(apiurl).then((response) => {
          let resdata = response.data;

          console.log(resdata);
          let labels = [];
          let vals = [];
          for(let i = 0; i < resdata.length; i++){
            console.log(resdata[i])
            labels.push(resdata[i].id);
            vals.push(resdata[i].score);
          }
          console.log(labels);
          console.log(vals);
          this.updateChart(labels, vals);
      })

      }
      return res;
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
    this.getApiData();
    this.chart = res;
  }
};
</script>

<style scoped  lang="scss"> 

  .chart{
    max-width: 300px;
    margin-left: auto;
    margin-right: auto;
  }

</style>
