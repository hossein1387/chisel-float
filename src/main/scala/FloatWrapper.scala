// scalastyle:off
package ChiselFloat

import chisel3._

// Wraps a Chisel Flo or Dbl datatype to allow easy
// extraction of the different parts (sign, exponent, mantissa)

class FloatWrapper(val num: Bits) {
    val (sign, exponent, mantissa, zero) = num.getWidth match {
        case 32 => (num(31).toBool(),
                    num(30, 23).asUInt(),
                    // if the exponent is 0
                    // this is a denormalized number
                    chisel3.util.Cat(Mux(num(30, 23) === 0.U,
                            0.U, 1.U),
                        num(22, 0).asUInt()),
                    num(30, 0) === 0.U)
        case 64 => (num(63).toBool(),
                    num(62, 52).asUInt(),
                    chisel3.util.Cat(Mux(num(62, 52) === 0.U,
                            0.U, 1.U),
                        num(51, 0).asUInt()),
                    num(62, 0) === 0.U)
    }
}
