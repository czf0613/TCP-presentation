<template>
  <div>
    <el-row v-if="fromMe">
      <el-col :span="12" :offset="11">
        <p>{{ content }}</p>
      </el-col>

      <el-col :span="1">
        <el-avatar shape="circle" size="medium" src="https://pic-bed.xyz/res/userFiles/czf/85.png"/>
      </el-col>
    </el-row>

    <el-row v-else>
      <el-col :span="1">
        <el-avatar shape="circle" size="medium" src="https://pic-bed.xyz/res/userFiles/czf/86.png"/>
      </el-col>

      <el-col :span="12">
        <p>{{ content }}</p>
      </el-col>
    </el-row>

    <el-divider/>
  </div>
</template>

<script>
export default {
  name: "Conversation",
  props: {
    fromMe: Boolean,
    content: String
  }
}
</script>

<style scoped>

</style>