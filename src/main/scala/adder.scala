package adder
import chisel3._
import _root_.circt.stage.ChiselStage
import chisel3._
import chisel3.util._

class FullAdder extends Module {
  val io = IO(new Bundle {
    val a = Input(Bool())  // ���� A
    val b = Input(Bool())  // ���� B
    val cin = Input(Bool()) // ǰһ���Ľ�λ
    val sum = Output(Bool()) // �����
    val cout = Output(Bool()) // ��λ���
  })

  // �߼����ʽ
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

    // ����4��ȫ������ֱ������
    val fas = Seq.fill(4)(Module(new FullAdder))

    // ֱ�����ӽ�λ������
    fas.zip(fas.tail).foreach { case (curr, next) => 
    next.io.cin := curr.io.cout 
    }

    // ��ʼ��λ
    fas(0).io.cin := io.cin

    // ����������������
    fas.zipWithIndex.foreach { case (fa, i) =>
        fa.io.a := io.a(i)
        fa.io.b := io.b(i)
    }

    // ʹ��Vec��asUInt��ȷת��
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