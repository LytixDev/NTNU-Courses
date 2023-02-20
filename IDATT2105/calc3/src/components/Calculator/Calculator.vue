<template>
  <h1>Calculator</h1>
  <div class="flex-container">
    <Display 
      data-testid="calculator-display"
      id="calculator-display"
      :value="source.length == 0 ? '0' : source"/>


    <div class="flex-line">
      <CalculatorButton value="C" @click="source = ''" data-testid="calculator-button-C" />
      <CalculatorButton value="ANS" @click="source = ''" data-testid="calculator-button-AC" />
      <CalculatorButton value="DEL" @click="source = source.substring(0, source.length - 1)" data-testid="calculator-button-DEL" />
      <CalculatorButton value="+" @click="sourceAppend('+')" data-testid="calculator-button-+" />
    </div>

    <div class="flex-line">
      <CalculatorButton value="1" @click="sourceAppend('1')" data-testid="calculator-button-1" />
      <CalculatorButton value="2" @click="sourceAppend('2')" data-testid="calculator-button-2" />
      <CalculatorButton value="3" @click="sourceAppend('3')" data-testid="calculator-button-3" />
      <CalculatorButton value="-" @click="sourceAppend('-')" data-testid="calculator-button--" />
    </div>

    <div class="flex-line">
      <CalculatorButton value="4" @click="sourceAppend('4')" data-testid="calculator-button-4" />
      <CalculatorButton value="5" @click="sourceAppend('5')" data-testid="calculator-button-5" />
      <CalculatorButton value="6" @click="sourceAppend('6')" data-testid="calculator-button-6" />
      <CalculatorButton value="*" @click="sourceAppend('*')" data-testid="calculator-button-*" />
    </div>

    <div class="flex-line">
      <CalculatorButton value="7" @click="sourceAppend('7')" data-testid="calculator-button-7" />
      <CalculatorButton value="8" @click="sourceAppend('8')" data-testid="calculator-button-8" />
      <CalculatorButton value="9" @click="sourceAppend('9')" data-testid="calculator-button-9" />
      <CalculatorButton value="/" @click="sourceAppend('/')" data-testid="calculator-button-/" />
    </div>

    <div class="flex-line">
      <CalculatorButton value=" " />
      <CalculatorButton value="0" @click="sourceAppend('0')" data-testid="calculator-button-0" />
      <CalculatorButton value="." @click="sourceAppend('.')" data-testid="calculator-button-." />
      <CalculatorButton value="=" @click="parseSource()" data-testid="calculator-button-=" />
    </div>

    <CalculatorLog :logArray="logArray" />

  </div>
</template>

<script>
import { defineComponent } from 'vue';
import CalculatorButton from "./CalculatorButton.vue"
import Display from './Display.vue';
import CalculatorLog from "./CalculatorLog.vue";
import CalculatorService from "@/services/CalculatorService"

class Expr {
  constructor(left, operator, right) {
    this.left = left;
    this.operator = operator;
    this.right = right;
  }
  evalExpr() {
    if (!(typeof this.left == NaN))
      var a = this.left
    else
      var a = this.left.evalExpr()
    if (!(typeof this.right == NaN))
      var b = this.right
    else
      var b = this.right.evalExpr()
    
    switch(this.operator) {
      case "+":
        return a + b
      case "-":
        return a - b
      case "*":
        return a * b
      case "/":
        return a / b
      default:
        return "Error"
    }
  }
}

export default defineComponent({
  name: "Calculator",
  components: {
    CalculatorButton,
    Display,
    CalculatorLog
  },
  data: () => ({
    source: "",
    logArray: []
  }),
  methods: {
    calculate() {
    },
    sourceAppend(c) {
      this.source += c
    },
    async parseSource() {
      let operators = ["+", "-", "*", "/"]
      let tokens = []
      let token = ""
      for (let c of this.source) {
        if (operators.includes(c)) {
          tokens.push(parseFloat(token))
          tokens.push(c)
          token = ""
        } else {
          token += c
        }
      }
      tokens.push(parseFloat(token))

      if (tokens.length != 3) {
        alert("Har ikke støtte for annet enn binære uttrykk")
        this.source = ""
        return
      }
      let expr = new Expr(tokens[0], tokens[1], tokens[2])
      //let result = expr.evalExpr().toString()
      let result = "";
      await CalculatorService.postCalculatorExpr(expr)
      .then((value) => {
        console.log(value.data)
        result = value.data;
      })
      .catch((err) => {
        console.error(err)
        result = err.data;
      });

      this.logArray.push(this.source + " = " + result)
      this.source = result
    }
  }
})
</script>

<style scoped>
.flex-container {
  width: fit-content;
  margin: auto;
}
.flex-line {
  display: flex;
  flex-direction: row;
  width: 100%;
  margin: 0 auto;
  justify-content: center;
}
</style>
