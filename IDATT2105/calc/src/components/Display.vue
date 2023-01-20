<template>
    <div class="display"> {{ value }} </div>
</template>

<script>
    export default {
        name: "Display",
        props: ["value"]
    }
</script>

<style scoped>
    .display {
        color: white;
        background-color: darkgray;
        padding: 10px;
        margin: 3px 3px 10px 3px;
        font-size: 32px;
    }
</style>