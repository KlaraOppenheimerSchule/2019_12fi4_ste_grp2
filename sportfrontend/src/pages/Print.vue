<template>
  <div class="printwrapper">
    <div class="page" v-for="student in students" :key="student.id">
      <div class="flexheader">
        <div class="klasse">{{ classDetails.name }}</div>
        <div class="logo">
          <img src="~assets/logo-klara.png" />
        </div>
        <div class="nummer">{{ student.id }}</div>
      </div>
      <div class="stationen">
        <div class="station" v-for="station in checkpoints" :key="station.id">
          <table>
            <tbody>
              <tr>
                <td>
                  <div class="checkbox"></div>
                </td>
                <td>{{ station.name }}</td>
              </tr>
              <tr>
                <td>{{ station.score }}</td>
                <td>{{ station.location }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "print",
  data: function() {
    return {
      students: [],
      checkpoints: [],
      classDetails: {}
    };
  },
  mounted: function() {
    //TODO: Move this to a seperate script
    let token = this.$q.cookies.get("token");
    let type = this.$q.cookies.get("type");

    if (token == undefined || type == undefined) {
      this.$router.push({ path: "/login" });
    }

    let apiurl = this.$api + "session/" + token + "/validate";

    this.$axios.get(apiurl).then(response => {
      let resdata = response.data;
      if (resdata[0].valid != true) {
        this.$router.push({ path: "/login" });
      }
    });

    this.getStudents();
  },
  methods: {
    getStudents: function() {
      let classid = this.$route.params.id;
      let apiurl = this.$api + "class/" + classid;

      this.$axios.get(apiurl).then(response => {
        let resdata = response.data;
        //TODO: Use Destructuring, because I don't have time
        this.classDetails.id = resdata[0].id;
        this.classDetails.name = resdata[0].name;
        this.classDetails.score = resdata[0].score;
        this.students = resdata[0].students;
        this.getCheckpoints();
      });
    },
    getCheckpoints: function() {
      let token = this.$q.cookies.get("token");
      let apiurl = this.$api + "checkpoint/all/" + token;

      this.$axios.get(apiurl).then(response => {
        let resdata = response.data;
        this.checkpoints = resdata;
        setTimeout(function() {
          window.print();
        }, 1000);
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.page {
  page-break-after: all;
  break-after: all;
  height: 100vh;
}
.flexheader {
  display: flex;
  justify-content: space-between;
  height: 100px;
  align-items: center;
  margin-bottom: 20px;
  img {
    height: 100px;
  }
}

.stationen {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}
.station {
  width: 21%;
  table {
    border: 1px solid black;
    width: 100%;
    tr:first-child td {
      border-bottom: 1px solid black;
    }
    tr td:first-child {
      border-right: 1px solid black;
    }
  }
}

.checkbox {
  border: 1px solid black;
  width: 10px;
  height: 10px;
}
</style>
