package adder
import chisel3._
import chiseltest._
import chisel3.util._
import org.scalatest.flatspec.AnyFlatSpec
import _root_.circt.stage.ChiselStage



class SimpleTest extends AnyFlatSpec with ChiselScalatestTester {
  "DUT" should "pass" in {
    test(new Adder)
    .withAnnotations(Seq(WriteVcdAnnotation))  { dut =>
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      dut.clock.step()

      val testCases = Seq(
          (0, 0),   // 0 + 0
          (1, 1),   // 1 + 1
          (2, 3),   // 2 + 3
          (4, 5),   // 4 + 5
          (8, 7)    // 8 + 7
      )

      for ((a, b) <- testCases) {
          dut.io.a.poke(a)               // 输入 a
          dut.io.b.poke(b)               // 输入 b
          println(dut.io.sum.peek())
          dut.io.sum.expect(a + b)       // 验证结果
      }
    }
  }
}
