package Ex0

import chisel3._
import chisel3.util.Counter

class Control(val rowsDim: Int, val colsDim: Int) extends Module {

  val io = IO(
    new Bundle {
      val inExecutionMode = Output(Bool())
      val counter = Output(UInt(32.W))

      val rowIdx = Output(UInt(32.W))
      val colIdx = Output(UInt(32.W))
    }
  )

  val counter = Counter(rowsDim * colsDim)
  val inExecutionModeRegister = RegInit(false.B)

  io.inExecutionMode := inExecutionModeRegister
  io.counter := counter.value

  when (counter.inc()) {
    inExecutionModeRegister := true.B
  }

  io.rowIdx := counter.value / colsDim.U
  io.colIdx := counter.value % colsDim.U
}
