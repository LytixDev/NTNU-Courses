<template>
    <div class="log">
        <p v-if="logArray" v-for="l of logArray.reverse()">{{ l }}</p>
    </div>
</template>

<script>
    export default {
        name: "CalculatorLog",
        props: ["logArray"]
    }
</script>

<style scoped>
    .log {
        height: 200px;
        border:1px solid #ccc;
        overflow:auto;
        margin: 10px;
    }
</style>
