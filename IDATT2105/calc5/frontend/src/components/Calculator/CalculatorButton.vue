<template>
    <div class="calculator-button">{{ value }}</div>
</template>

<script>
    export default {
        name: "CalculatorButton",
        props: ["value"]
    }
</script>

<style scoped>
    .calculator-button {
        width: 100px;
        color: white;
        background-color: gray;
        margin: 3px;
        padding: 5px;
    }

    .calculator-button:hover {
        background-color: darkgray;
    }

    .calculator-button:active {
        background-color: blue;
    }
</style>
