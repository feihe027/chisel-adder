package adder
import chisel3._
import _root_.circt.stage.ChiselStage
import chisel3._
import chisel3.util._

class FullAdder extends Module {
  val io = IO(new Bundle {
    val a = Input(Bool())  // 输入 A
    val b = Input(Bool())  // 输入 B
    val cin = Input(Bool()) // 前一级的进位
    val sum = Output(Bool()) // 和输出
    val cout = Output(Bool()) // 进位输出
  })

  // 逻辑表达式
  io.sum := io.a ^ io.b ^ io.cin
  io.cout := (io.a & io.b) | (io.cin & (io.a ^ io.b))
}


class CarryLogicModule(width: Int) extends Module {
  val io = IO(new Bundle {
    val p = Input(UInt(width.W))  // 传播信号
    val g = Input(UInt(width.W))  // 生成信号
    val cin = Input(Bool())       // 初始进位
    val c = Output(UInt((width + 1).W))  // 进位输出
  })

  val c = RegInit(VecInit(Seq.fill(width + 1)(false.B)))
  c(0) := io.cin

  val pVec = VecInit(io.p.asBools)
  val gVec = VecInit(io.g.asBools)

  for (i <- 0 until width) {
    c(i + 1) := gVec(i) | (pVec(i) & c(i))
  }

  io.c := c.asUInt
}

class CarryLookAheadAdder(width: Int) extends Module {
  require(width > 0, "Width must be positive")

  val io = IO(new Bundle {
    val a = Input(UInt(width.W))
    val b = Input(UInt(width.W))
    val cin = Input(Bool())
    val sum = Output(UInt(width.W))
    val cout = Output(Bool())
  })

  // 生成和校验信号，添加注释说明
  val p = VecInit((0 until width).map { i => 
    // 传播信号：相同位异或
    io.a(i) ^ io.b(i) 
  })
  
  val g = VecInit((0 until width).map { i => 
    // 生成信号：相同位与
    io.a(i) & io.b(i) 
  })

  val carryLogic = Module(new CarryLogicModule(width))
  carryLogic.io.p := p.asUInt
  carryLogic.io.g := g.asUInt
  carryLogic.io.cin := io.cin

  // 使用 zipWithIndex 简化和的计算
  val sum = VecInit(p.zip(carryLogic.io.c.asBools).map { 
    case (pi, ci) => pi ^ ci 
  })

  io.sum := sum.asUInt
  io.cout := carryLogic.io.c(width)
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