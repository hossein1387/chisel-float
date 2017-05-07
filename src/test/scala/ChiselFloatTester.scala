package ChiselFloat

import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import FloatUtils.{floatToBigInt, doubleToBigInt}

class FPMult32Test(c: FPMult32) extends PeekPokeTester(c) {
    var lastExpected = 0.0f

    poke(c.io.a, floatToBigInt(0.0f))
    poke(c.io.b, floatToBigInt(3.0f))
    step(1)

    for (i <- 0 until 8) {
        val a = rnd.nextFloat() * 10000.0f - 5000.0f
        val b = rnd.nextFloat() * 10000.0f - 5000.0f
        val expected = a * b

        poke(c.io.a, floatToBigInt(a))
        poke(c.io.b, floatToBigInt(b))
        step(1)

        println(s"Expecting $lastExpected or ${floatToBigInt(lastExpected)}")

        expect(c.io.res, floatToBigInt(lastExpected))
        lastExpected = expected
    }

    step(1)

    expect(c.io.res, floatToBigInt(lastExpected))
}

class FPMult64Test(c: FPMult64) extends PeekPokeTester(c) {
    var lastExpected = 0.0

    for (i <- 0 until 8) {
        val a = rnd.nextDouble() * 10000.0 - 5000.0
        val b = rnd.nextDouble() * 10000.0 - 5000.0
        val expected = a * b

        poke(c.io.a, doubleToBigInt(a))
        poke(c.io.b, doubleToBigInt(b))
        step(1)

        if (i > 0) {
            expect(c.io.res, doubleToBigInt(lastExpected))
        }
        lastExpected = expected
    }

    step(1)

    expect(c.io.res, doubleToBigInt(lastExpected))
}

class ChiselFloatTester extends ChiselFlatSpec {
  private val backendNames = Array[String]("firrtl"/*, "verilator"*/)
  for ( backendName <- backendNames ) {
    "ChiselFloat" should s"Implementation of a floating point multiplier (with $backendName)" in {
      Driver(() => new FPMult32, backendName) {
        c => new FPMult32Test(c)
      } should be (true)
    }
  }
}

