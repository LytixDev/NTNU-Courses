package Ex0

import chisel3._
import chisel3.util.Counter
import chisel3.experimental.MultiIOModule

class MatMul(val rowDimsA: Int, val colDimsA: Int) extends MultiIOModule {

  val io = IO(
    new Bundle {
      val dataInA     = Input(UInt(32.W))
      val dataInB     = Input(UInt(32.W))

      val dataOut     = Output(UInt(32.W))
      val outputValid = Output(Bool())
    }
  )

  val debug = IO(
    new Bundle {
      val myDebugSignal = Output(Bool())
    }
  )


  /**
    * Your code here
    */
  val matrixA     = Module(new Matrix(rowDimsA, colDimsA)).io
  val matrixB     = Module(new Matrix(rowDimsA, colDimsA)).io
  val dotProdCalc = Module(new DotProd(colDimsA)).io
  val control     = Module(new Control(rowDimsA, colDimsA)).io

  val rowA = RegInit(UInt(32.W), 0.U)
  val rowB = RegInit(UInt(32.W), 0.U)
  val col = RegInit(UInt(32.W), 0.U)

  /* Wire the outputs */
  io.dataOut := dotProdCalc.dataOut
  when (control.inExecutionMode && dotProdCalc.outputValid) {
    printf("TRUE: %d\n", dotProdCalc.dataOut)
    io.outputValid := true.B
  } otherwise {
    io.outputValid := false.B
  }


  /* Setup mode */
  matrixA.dataIn := io.dataInA
  matrixB.dataIn := io.dataInB

  matrixA.rowIdx := control.rowIdx
  matrixA.colIdx := control.colIdx
  matrixB.rowIdx := control.rowIdx
  matrixB.colIdx := control.colIdx

  matrixA.writeEnable := !control.inExecutionMode
  matrixB.writeEnable := !control.inExecutionMode

  /* Execution mode */
  dotProdCalc.dataInA := matrixA.dataOut
  dotProdCalc.dataInB := matrixB.dataOut

  when (control.inExecutionMode) {
    matrixA.rowIdx := rowA
    matrixB.rowIdx := rowB

    matrixB.colIdx := col
    matrixA.colIdx := col

    col := col + 1.U
    when (col === colDimsA.U - 1.U) {
      col := 0.U
      rowB := rowB + 1.U
      when (rowB === rowDimsA.U - 1.U) {
        rowB := 0.U
        rowA := rowA + 1.U
      }
    }
    printf("%d %d %d\n", rowA, rowB, col)
  }

  debug.myDebugSignal := false.B
}