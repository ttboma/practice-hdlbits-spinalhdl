package hdlbits.verilog_language

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsModuleAddSub extends App {
  Config
    .spinal("ModuleAddSub.v") // set the output file name
    .generateVerilog(HdlBitsModuleAddSub())
}

case class HdlBitsAdd16() extends Component {
  val io = new Bundle {

    val a, b = in Bits (16 bits)
    val cin = in Bool ()
    val sum = out Bits (16 bits)
    val cout = out Bool ()
  }

  io.sum := (io.a.asUInt + io.b.asUInt + io.cin.asUInt).asBits
  io.cout := (io.a(15) ^ io.b(15)) & ~io.sum(15)

  def drive(a: Bits, b: Bits, cin: Bool, sum: Bits, cout: Bool) =
    new Area {
      io.a := a
      io.b := b
      io.cin := cin
      sum := io.sum
      cout := io.cout
    }
}

object HdlBitsAdd16 {
  def apply(): HdlBitsAdd16 = {
    val rtl = new HdlBitsAdd16()
    setNames(rtl)
    rtl
  }

  private def setNames(mod: HdlBitsAdd16) {
    mod.setDefinitionName("add16")
    mod.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
  }
}

// https://hdlbits.01xz.net/wiki/Module_addsub
// module top_module(
//     input [31:0] a,
//     input [31:0] b,
//     input sub,
//     output [31:0] sum
// );
//     wire [31:0] inv_b;
//     assign inv_b = b ^ {32{sub}};
//
//     wire cout, d;
//     add16 lo(a[15:0], inv_b[15:0], sub, sum[15:0], cout);
//     add16 hi(a[31:16], inv_b[31:16], cout, sum[31:16], d);
//
// endmodule
case class HdlBitsModuleAddSub() extends Component {
  val io = new Bundle {
    val a, b = in Bits (32 bits)
    val sub = in Bool ()
    val sum = out Bits (32 bits)
  }

  val inv_b = Bits(32 bits)
  val cout, d = Bool()

  inv_b := io.b ^ (io.sub.asBits #* 32)

  val lo, hi = HdlBitsAdd16()

  lo.drive(
    io.a(15 downto 0),
    inv_b(15 downto 0),
    io.sub,
    io.sum(15 downto 0),
    cout
  )

  hi.drive(
    io.a(31 downto 16),
    inv_b(31 downto 16),
    cout,
    io.sum(31 downto 16),
    d
  )
}

object HdlBitsModuleAddSub {
  def apply(): HdlBitsModuleAddSub = {
    val rtl = new HdlBitsModuleAddSub()
    setNames(rtl)
    rtl
  }

  private def setNames(mod: HdlBitsModuleAddSub) {
    mod.setDefinitionName("top_module")
    mod.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
  }
}
