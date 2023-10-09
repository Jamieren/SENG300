package com.thelocalmarketplace.hardware.test;


import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import com.tdc.coin.CoinDispenser;
import ca.ucalgary.seng300.simulation.SimulationException;


public class CoinDispenserTest {

    private CoinDispenser dispenser;

    @Before
    public void setUp() {
        dispenser = new CoinDispenser(100);
    }

    @Test(expected = SimulationException.class)
    public void testNegativeCapacity() {
        new CoinDispenser(-10);
    }

    @Test(expected = SimulationException.class)
    public void testZeroCapacity() {
        new CoinDispenser(0);
    }

    @Test 
    public void testHasSpaceInitial() {
        assertTrue(dispenser.hasSpace());
    }

    @Test(expected = SimulationException.class)
    public void testReceiveNullCoin() throws Exception {
        dispenser.receive(null);
    }


}
