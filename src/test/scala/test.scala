package adder
import chisel3._
import chiseltest._
import chisel3.util._
import org.scalatest.flatspec.AnyFlatSpec
import _root_.circt.stage.ChiselStage


class SimpleTest extends AnyFlatSpec with ChiselScalatestTester {
    behavior of "Adder4Bit"

    it should "correctly add two 4-bit numbers without carry in" in {
        test(new CarryLookAheadAdder(4)) { dut =>
            // 测试无初始进位的情况
            dut.io.a.poke(5.U)     // 二进制 0101
            dut.io.b.poke(3.U)     // 二进制 0011
            dut.io.cin.poke(false.B)
            dut.clock.step(1)
            
            // 预期结果：5 + 3 = 8
            dut.io.sum.expect(8.U)
            println("5 + 3 = ", dut.io.sum.peekInt())
            dut.io.cout.expect(false.B)
        }
    }

    it should "correctly add two 4-bit numbers with carry in" in {
        test(new CarryLookAheadAdder(4)) { dut =>
            // 测试有初始进位的情况
            dut.io.a.poke(15.U)    // 二进制 1111
            dut.io.b.poke(1.U)     // 二进制 0001
            dut.io.cin.poke(true.B)
            dut.clock.step(1)
            
            // 预期结果：15 + 1 + 1(进位) = 17 (超出4位)
            dut.io.sum.expect(1.U)  // 低4位
            dut.io.cout.expect(true.B)
        }
    }

    it should "handle maximum 4-bit addition" in {
        test(new CarryLookAheadAdder(4)) { dut =>
            // 测试最大值相加
            dut.io.a.poke(15.U)    // 二进制 1111
            dut.io.b.poke(15.U)    // 二进制 1111
            dut.io.cin.poke(false.B)
            dut.clock.step(1)
            
            // 预期结果：15 + 15 = 30
            dut.io.sum.expect(14.U)  // 低4位 1110
            dut.io.cout.expect(true.B)
        }
    }

    it should "handle zero addition" in {
        test(new CarryLookAheadAdder(4)) { dut =>
            // 测试零值相加
            dut.io.a.poke(0.U)
            dut.io.b.poke(0.U)
            dut.io.cin.poke(false.B)
            dut.clock.step(1)
            
            dut.io.sum.expect(0.U)
            dut.io.cout.expect(false.B)
        }
    }
}