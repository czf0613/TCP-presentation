<template>
  <el-container>
    <el-aside>
      <el-menu class="el-menu-vertical-demo" :default-active=String(1) @select="handleSelect">
        <el-menu-item index="1">HTTP Introduction</el-menu-item>

        <el-menu-item index="2">HTTP Method test</el-menu-item>

        <el-menu-item index="3">WebSocket</el-menu-item>
      </el-menu>
    </el-aside>

    <el-main>
      <Intro v-if="page===1"/>
      <HTTP v-else-if="page===2"/>
      <WS v-else-if="page===3"/>
    </el-main>
  </el-container>
</template>

<script>
import Intro from "@/components/Intro"
import HTTP from "@/components/HTTP"
import WS from "@/components/WS"

export default {
  name: "Home",
  components: {
    Intro,
    HTTP,
    WS
  },
  data() {
    return {
      page: 1
    }
  },
  methods: {
    handleSelect(key) {
      this.page = parseInt(key)
    }
  }
}
</script>

<style scoped>

</style>