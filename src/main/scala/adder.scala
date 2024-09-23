package adder
import chisel3._
import _root_.circt.stage.ChiselStage


class Adder extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(4.W))      // 4-bit input a
    val b = Input(UInt(4.W))      // 4-bit input b
    val sum = Output(UInt(5.W))    // 5-bit output sum (to accommodate carry)
  })

  io.sum := io.a + io.b            
}

object Main extends App {
  emitVerilog(
    new Adder,
    Array(
      "--emission-options=disableMemRandomization,disableRegisterRandomization",
      "--emit-modules=verilog", 
      "--info-mode=use",
      "--target-dir=hdl",
      "--full-stacktrace",
      // "--help"
    )
  )
}