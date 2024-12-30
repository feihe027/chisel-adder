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

class Adder4Bit extends Module {  
    val io = IO(new Bundle {  
    val a = Input(UInt(4.W))  
    val b = Input(UInt(4.W))  
    val cin = Input(Bool())   
    val sum = Output(UInt(4.W))   
    val cout = Output(Bool())  
    })  

    // 创建4个全加器并直接连接
    val fas = Seq.fill(4)(Module(new FullAdder))

    // 直接连接进位和输入
    fas.zip(fas.tail).foreach { case (curr, next) => 
    next.io.cin := curr.io.cout 
    }

    // 初始进位
    fas(0).io.cin := io.cin

    // 并行连接输入和输出
    fas.zipWithIndex.foreach { case (fa, i) =>
        fa.io.a := io.a(i)
        fa.io.b := io.b(i)
    }

    // 使用Vec和asUInt正确转换
    io.sum := VecInit(fas.map(_.io.sum)).asUInt
    io.cout := fas.last.io.cout
}

object Main extends App {
  emitVerilog(
    new Adder4Bit,
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