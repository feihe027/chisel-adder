package adder
import chisel3._
import _root_.circt.stage.ChiselStage
import chisel3._
import chisel3.util._

// 全加法器模块
class FullAdder extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(1.W))
    val b = Input(UInt(1.W))
    val cin = Input(UInt(1.W))
    val sum = Output(UInt(1.W))
    val generate = Output(UInt(1.W))
    val propagate = Output(UInt(1.W))
  })

  // 生成信号 G = a & b
  io.generate := io.a & io.b

  // 传播信号 P = a ^ b
  io.propagate := io.a ^ io.b

  // 和 Sum = a ^ b ^ cin
  io.sum := io.a ^ io.b ^ io.cin
}

// 参数化超前进位加法器
class CarryLookAheadAdder(width: Int) extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(width.W))
    val b = Input(UInt(width.W))
    val cin = Input(UInt(1.W))
    val sum = Output(UInt(width.W))
    val cout = Output(UInt(1.W))
  })

  // 创建进位和生成、传播信号的线网
  val carries = Wire(Vec(width + 1, UInt(1.W)))
  val generates = Wire(Vec(width, UInt(1.W)))
  val propagates = Wire(Vec(width, UInt(1.W)))
  val sums = Wire(Vec(width, UInt(1.W)))

  // 初始进位
  carries(0) := io.cin

  // 连接全加法器并计算进位
  for (i <- 0 until width) {
    val fullAdder = Module(new FullAdder())
    // 连接输入
    fullAdder.io.a := io.a(i)
    fullAdder.io.b := io.b(i)
    fullAdder.io.cin := carries(i)

    // 保存生成和传播信号
    generates(i) := fullAdder.io.generate
    propagates(i) := fullAdder.io.propagate
    sums(i) := fullAdder.io.sum

    // 计算下一位进位
    // 进位计算公式：Ci+1 = Gi + (Pi * Ci)
    carries(i + 1) := generates(i) | (propagates(i) & carries(i))
  }

  // 输出和和最高位进位
  io.sum := sums.asUInt
  io.cout := carries(width)
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