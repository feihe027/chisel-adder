package adder
import chisel3._
import _root_.circt.stage.ChiselStage
import chisel3._
import chisel3.util._


class CarryLookAheadAdder(width: Int) extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(width.W))
    val b = Input(UInt(width.W))
    val cin = Input(Bool())
    val sum = Output(UInt(width.W))
    val cout = Output(Bool())
  })
  require(width > 0, "Width must be positive")

  // 生成信号和传播信号
  val G = io.a & io.b
  val P = io.a ^ io.b

  // 进位信号
  val C = Wire(Vec(width + 1, UInt(1.W)))
  C(0) := io.cin

  for (i <- 0 until width) {
    C(i + 1) := G(i) | (P(i) & C(i))
  }

  // 和信号
  io.sum := P ^ C.asUInt(width - 1, 0)

  // 输出进位
  io.cout := C(width)
}


object Main extends App {
  emitVerilog(
    new CarryLookAheadAdder(4),
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