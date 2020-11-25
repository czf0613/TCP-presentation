<template>
  <div>
    <el-row class="rowCenter">
      <el-col :span="8" v-if="!connected">
        <el-input placeholder="enter a nick name to join" v-model="userName"/>
      </el-col>

      <el-col :span="6" v-if="!connected">
        <el-button :loading="joining" v-on:click=connect type="primary">Connect</el-button>
      </el-col>

      <el-col :span="6">
        <el-button v-on:click=disConnect type="warning">Disconnect</el-button>
      </el-col>
    </el-row>

    <el-divider/>

    <div v-if="connected">
      <el-row class="rowCenter">
        <el-col :span="8">
          <el-input placeholder="say some thing" v-model="someThing"/>
        </el-col>

        <el-col :span="6">
          <el-button v-on:click=sendMsg type="primary">Send</el-button>
        </el-col>
      </el-row>

      <el-divider/>
    </div>

    <Conversation v-for="i in talks" :from-me="i.me" :content="i.content" :key="i.content"/>
  </div>
</template>

<script>
import Conversation from "@/components/Conversation"

export default {
  name: "WS",
  data() {
    return {
      joining: false,
      connected: false,
      userName: '',
      talks: [{
        me: false,
        content: 'Welcome~'
      }],
      ws: undefined,
      someThing: ''
    }
  },
  components: {
    Conversation
  },
  methods: {
    connect() {
      if (this.connected) {
        alert('you have already joined a socket!')
        return
      }
      if (this.userName.length === 0) {
        alert('Wrong user name')
        return
      }
      this.joining = true

      this.ws = new WebSocket(`${this.GLOBAL.endPoint}/${this.userName}`)
      let outer = this
      this.ws.onopen = () => {
        this.connected = true
        this.joining = false
      }
      this.ws.onmessage = (evt) => {
        outer.talks.unshift({
          me: false,
          content: evt.data
        })
      }
      this.ws.onclose = () => {
        outer.talks.unshift({
          me: false,
          content: 'connection closed'
        })
        this.connected = false
        this.ws = undefined
      }
      this.ws.onerror = (err) => {
        console.log(err)
        alert('Error occurs')
      }

      this.connected = true
      this.joining = false
    },
    disConnect() {
      if (!this.connected) {
        alert('you have no connection')
        return
      }
      if (this.ws === undefined) {
        alert('you have no connection')
        return
      }

      this.ws.close()
    },
    sendMsg() {
      if (this.ws === undefined) {
        alert('you have no connection')
        return
      }
      this.talks.unshift({
        me: true,
        content: this.someThing
      })
      let out = this
      setTimeout(() => {
        out.ws.send(this.someThing)
        out.someThing = ''
      }, 500)
    }
  }
}
</script>

<style scoped>

</style>