<template>
  <div>
    <h2>Test GET method</h2>
    <el-row class="rowCenter">
      <el-col :span="8">
        <el-input placeholder="enter a nick name to test" v-model="getName"/>
      </el-col>

      <el-col :span="6">
        <el-button :loading="getting" v-on:click=getFromServer type="primary">GET!</el-button>
      </el-col>
    </el-row>
    <div v-if="gets.length!==0">
      <h4>Response:</h4>
      <p>{{ gets }}</p>
    </div>

    <el-divider/>

    <h2>Test POST method</h2>
    <el-upload
        class="upload-demo"
        drag
        :on-success=uploadSuccess
        :on-error=uploadFail
        :action="uploadURL">
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">Drag your file here or <em>click to choose</em></div>
      <div class="el-upload__tip" slot="tip">No more than 1GB</div>
    </el-upload>
    <div v-if="posted">
      <h4>Response:</h4>
      <p>File Name: {{ posts.fileName }}</p>
      <p>File Type: {{ posts.type }}</p>
      <p>File Size: {{ posts.size }} bytes</p>
      <p>Timestamp: {{ posts.time }}</p>
    </div>

    <el-divider/>

    <h2>Test DELETE method</h2>
    <el-row class="rowCenter">
      <el-col :span="8">
        <el-input placeholder="enter a nick name to test" v-model="deleteName"/>
      </el-col>

      <el-col :span="6">
        <el-button :loading="deleting" v-on:click=deleteFromServer type="danger">DELETE!</el-button>
      </el-col>
    </el-row>
    <div v-if="deletes.length!==0">
      <h4>Response:</h4>
      <p>{{ deletes }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "HTTP",
  data() {
    return {
      getName: '',
      getting: false,
      gets: '',
      uploadURL: `${this.GLOBAL.domain}/tcp/post`,
      posted: false,
      posts: {
        fileName: '',
        type: '',
        size: 0,
        time: ''
      },
      deleteName: '',
      deleting: false,
      deletes: ''
    }
  },
  methods: {
    getFromServer() {
      if (this.getName.length === 0) {
        alert('Wrong Parameter!')
        return
      }
      this.getting = true
      this.GLOBAL.fly.get(`${this.GLOBAL.domain}/tcp/get/${this.getName}`)
          .then(response => {
            this.gets = response.data
            this.getting = false
          })
          .catch(error => {
            console.log(error)
            this.getting = false
          })
    },
    uploadFail() {
      alert('Upload failed')
    },
    uploadSuccess(response) {
      this.posted = true
      this.posts = response
    },
    deleteFromServer() {
      if (this.deleteName.length === 0) {
        alert('Wrong Parameter!')
        return
      }
      this.deleting = true
      this.GLOBAL.fly.delete(`${this.GLOBAL.domain}/tcp/delete/${this.deleteName}`)
          .then(response => {
            this.deletes = response.data
            this.deleting = false
          })
          .catch(error => {
            console.log(error)
            this.deleting = false
          })
    }
  }
}
</script>

<style scoped>
.rowCenter {
  display: flex;
  justify-content: center;
  flex-direction: row;
  align-items: center;
}
</style>