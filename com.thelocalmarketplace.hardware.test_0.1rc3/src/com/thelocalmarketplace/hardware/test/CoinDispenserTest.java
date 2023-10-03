package com.thelocalmarketplace.hardware.test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import com.tdc.coin.CoinDispenser;
import com.tdc.CashOverloadException;
import com.tdc.coin.Coin;
import ca.ucalgary.seng300.simulation.SimulationException;



public class CoinDispenserTest {

    private CoinDispenser dispenser;

    @Before
    public void setUp() {
        dispenser = new CoinDispenser(10);
    }

    @Test(expected = SimulationException.class)
    public void testNegativeCapacity() {
        new CoinDispenser(-10);
    }

    @Test
    public void testHasSpaceInitial() {
        assertTrue(dispenser.hasSpace());
    }

    @Test(expected = CashOverloadException.class)
    public void testOverload() throws Exception {
        for(int i = 0; i < 11; i++) {
            dispenser.receive(new Coin(null));
        }
    }

    @Test
    public void testEmitCoin() throws Exception {
        dispenser.receive(new Coin(null));
        dispenser.emit();
    }

    @Test
    public void testLoadAndUnload() throws Exception {
        dispenser.load(new Coin(null), new Coin(null));
        dispenser.unload();
    }

}
