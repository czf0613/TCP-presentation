<template>
  <div>
    <PDF
        v-for="i in numPages"
        :key="i"
        :src="source"
        :page="i"
    />
  </div>
</template>

<script>
import PDF from 'vue-pdf'

let loadingTask = PDF.createLoadingTask('/static/TCP.pdf')

export default {
  name: "Intro",
  components: {
    PDF
  },
  data() {
    return {
      numPages: 0,
      source: loadingTask
    }
  },
  mounted() {
    this.source.promise.then(pdf => {
      this.numPages = pdf.numPages;
    })
    .catch(err => {
      console.log(err)
    })
  }
}
</script>

<style scoped>

</style>