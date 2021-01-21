package net.oddware.alcolator

import org.junit.Assert
import org.junit.Test

class DrinkTest {
    @Test
    fun testVolumeAsDL() {
        Assert.assertEquals(
            "100 ML should be 1.0 DL",
            1.0,
            Drink(volumeML = 100).volumeAsDL(),
            0.0
        )
        Assert.assertEquals(
            "250 ML should be 2.5 DL",
            2.5,
            Drink(volumeML = 250).volumeAsDL(),
            0.0
        )
    }

    @Test
    fun testVolumeAsL() {
        Assert.assertEquals(
            "1000 ML should be 1.0 L",
            1.0,
            Drink(volumeML = 1000).volumeAsL(),
            0.0
        )
        Assert.assertEquals(
            "2500 ML should be 2.5 L",
            2.5,
            Drink(volumeML = 2500).volumeAsL(),
            0.0
        )
    }

    @Test
    fun testAlcML() {
        Assert.assertEquals(
            "1000 ML drink @ 37.5% should contain 375 ML alcohol",
            375.0,
            Drink(volumeML = 1000, alcPct = 37.5).alcML(),
            0.0
        )
        Assert.assertEquals(
            "60 ML drink @ 37.5% should contain 22.5 ML alcohol",
            22.5,
            Drink(volumeML = 60, alcPct = 37.5).alcML(),
            0.0
        )
        Assert.assertEquals(
            "500 ML drink @ 6.4% should contain 32 ML alcohol",
            32.0,
            Drink(volumeML = 500, alcPct = 6.4).alcML(),
            0.0
        )
    }

    @Test
    fun testWaterML() {
        Assert.assertEquals(
            "1000 ML drink @ 37.5% should contain 625 ML water",
            625.0,
            Drink(volumeML = 1000, alcPct = 37.5).waterML(),
            0.0
        )
        Assert.assertEquals(
            "500 ML drink @ 6.4% should contain 468 ML water",
            468.0,
            Drink(volumeML = 500, alcPct = 6.4).waterML(),
            0.0
        )
    }

    @Test
    fun testPricePerAlcML() {
        Assert.assertEquals(
            "60 ML drink @ 37.5% priced 102.0 should cost 4.53 per ML alcohol",
            4.53,
            Drink(volumeML = 60, alcPct = 37.5, price = 102.0).pricePerAlcML(),
            0.01 // will actually be 4.533333333 (etc)
        )
        Assert.assertEquals(
            "500 ML drink @ 6.4% priced 69.0 should cost 2.15625 per ML alcohol",
            2.15625,
            Drink(volumeML = 500, alcPct = 6.4, price = 69.0).pricePerAlcML(),
            0.0
        )
    }

    @Test
    fun testPricePerDrinkML() {
        Assert.assertEquals(
            "60 ML drink @ 37.5% priced 102.0 should cost 1.7 per ML drink",
            1.7,
            Drink(volumeML = 60, alcPct = 37.5, price = 102.0).pricePerDrinkML(),
            0.0
        )
        Assert.assertEquals(
            "500 ML drink @ 6.4% priced 69.0 should cost 0.138 per ML drink",
            0.138,
            Drink(volumeML = 500, alcPct = 6.4, price = 69.0).pricePerDrinkML(),
            0.0
        )
    }

    @Test
    fun testMatchAlcML() {
        val d1 = Drink()
        val d2 = Drink()

        Assert.assertEquals(
            "Two empty drinks should return 0.0",
            0.0,
            d1.matchAlcML(d2),
            0.0
        )

        // If we have two identical drinks, the result should be the same as either drink's volume
        d1.volumeML = 500
        d1.alcPct = 6.4
        d2.volumeML = d1.volumeML
        d2.alcPct = d1.alcPct
        Assert.assertEquals(
            "Two drinks with same percentage and volume should return the same value as the drink volume",
            d1.volumeML.toDouble(),
            d1.matchAlcML(d2),
            0.0
        )

        // If we have two drinks of the same percentage, where the first is twice the volume
        // of the second, we should get the volume of the first as a result
        d1.volumeML = 1000
        d1.alcPct = 6.4
        d2.volumeML = 500
        d2.alcPct = d1.alcPct
        Assert.assertEquals(
            "Identical liquid with double the volume, should return the double volume",
            d1.volumeML.toDouble(),
            d1.matchAlcML(d2),
            0.0
        )

        // Sitting vs Sleepy
        d1.volumeML = 500
        d1.alcPct = 6.4
        d2.volumeML = 500
        d2.alcPct = 4.8
        Assert.assertEquals(
            "You need 666.666 ML of 4.8% pct drink to match 500 ML of 6.4% drink",
            666.666,
            d1.matchAlcML(d2),
            0.001
        )

        // Sitting vs Jack
        d1.volumeML = 500
        d1.alcPct = 6.4
        d2.volumeML = 60
        d2.alcPct = 37.5
        Assert.assertEquals(
            "You need 85.3 ML of 37.5% drink to match 500 ML of 6.4% drink",
            85.3,
            d1.matchAlcML(d2),
            0.1
        )

        // Sitting vs Red Wine
        d1.volumeML = 500
        d1.alcPct = 6.4
        d2.volumeML = 200
        d2.alcPct = 12.5
        Assert.assertEquals(
            "You need 256.0 ML of 12.5% drink to match 500 ML of 6.4% drink",
            256.0,
            d1.matchAlcML(d2),
            0.0
        )

        // Double the amount for half the percentage
        d1.volumeML = 100
        d1.alcPct = 10.0
        d2.volumeML = 100
        d2.alcPct = 5.0
        Assert.assertEquals(
            "You need 200 ML of 5.0% drink to match 100 ML of 10.0% drink",
            200.0,
            d1.matchAlcML(d2),
            0.0
        )

        // How much Sitting for a bottle of Alamos red wine
        d1.volumeML = 750
        d1.alcPct = 13.0
        d2.volumeML = 500
        d2.alcPct = 6.4
        Assert.assertEquals(
            "You need 1523.4375 ML 6.4% to match 750 ML of 13% drink",
            1523.4375,
            d1.matchAlcML(d2),
            0.0
        )
    }

    @Test
    fun testMatchHowMany() {
        val d1 = Drink()
        val d2 = Drink()

        // Double the amount for half the percentage
        d1.volumeML = 500
        d1.alcPct = 10.0
        d2.volumeML = 500
        d2.alcPct = 5.0
        Assert.assertEquals(
            "You need 2X of a drink with half the percentage, given the volume is the same",
            2.0,
            d1.matchHowMany(d2),
            0.0
        )

        // Sitting vs 40% shot
        d1.volumeML = 500
        d1.alcPct = 6.4
        d2.volumeML = 40
        d2.alcPct = 40.0
        Assert.assertEquals(
            "You need 2X 40 ML 40% drink to match 500 ML 6.4%",
            2.0,
            d1.matchHowMany(d2),
            0.0
        )

        // How many 1 DL glasses of 12% wine to match 500 ML Sitting @ 6.4%
        d1.volumeML = 500
        d1.alcPct = 6.4
        d2.volumeML = 100
        d2.alcPct = 12.0
        Assert.assertEquals(
            "You need 2.6 X 100 ML 12% drink to match 500 ML 6.4%",
            2.6,
            d1.matchHowMany(d2),
            0.1
        )

        // How many Sitting for a bottle of Alamos red wine
        d1.volumeML = 750
        d1.alcPct = 13.0
        d2.volumeML = 500
        d2.alcPct = 6.4
        Assert.assertEquals(
            "You need 3.046875 X 500 ML 6.4% to match 750 ML of 13% drink",
            3.046875,
            d1.matchHowMany(d2),
            0.0
        )
    }

    @Test
    fun testMatchDrink() {
        val d1 = Drink()
        val d2 = Drink()

        // Match a bottle of Alamos red wine against Sitting Bulldog
        d1.volumeML = 750
        d1.alcPct = 13.0
        d2.volumeML = 500
        d2.alcPct = 6.4
        val res = d1.matchDrink(d2)
        Assert.assertEquals(
            "You need 3.046875 X 500 ML 6.4% to match 750 ML of 13% drink",
            3.046875,
            res.times,
            0.0
        )
        Assert.assertEquals(
            "You need 1523.4375 ML 6.4% to match 750 ML of 13% drink",
            1523.4375,
            res.ml,
            0.0
        )
    }
}