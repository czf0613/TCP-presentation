<template>
  <div id="app">
    <lottie :options="playOptions" :height="250" :width="250"/>
    <el-divider/>
    <Home/>
  </div>
</template>

<script>
import Home from './components/pages/Home'
import animationData from './assets/anim.json'

export default {
  name: 'App',
  components: {
    Home
  },
  data(){
    return {
      playOptions: {
        animationData: animationData,
        loop: true,
        autoPlay: true
      }
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
