package com.thelocalmarketplace.hardware.test;


import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinDispenser;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;


public class CoinDispenserTest {

    private CoinDispenser dispenser;
    private Coin coin25Cents;  // USD 0.25
    private Coin coin1Dollar;  // USD 1.00

    @Before
    public void setUp() {
        dispenser = new CoinDispenser(10);
        dispenser.activate(); //assume the unit is activated
        Coin.DEFAULT_CURRENCY = Currency.getInstance("USD");  // set default currency for Coin
        coin25Cents = new Coin(new BigDecimal("0.25")); // 25 cents coin
        coin1Dollar = new Coin(new BigDecimal("1.00")); // 1 dollar coin
    }
    
    @Test
    public void testReceiveValidCoin() throws CashOverloadException, DisabledException {
        dispenser.receive(coin1Dollar);
        assertTrue(dispenser.hasSpace());
    }
    
    @Test
    public void testReceiveWhenFull() throws CashOverloadException, DisabledException {
        int capacity = dispenser.getCapacity();
        for (int i = 0; i < capacity; i++) {
            dispenser.receive(coin1Dollar);
        }
        assertThrows(CashOverloadException.class, () -> dispenser.receive(coin1Dollar));
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
    
    @Test (expected = NoPowerException.class)
    public void testReceiveWhenDisabled() throws CashOverloadException, DisabledException {
        dispenser.disable(); // Simulate a disabled dispenser
        dispenser.receive(coin1Dollar);
    }
    
    @Test
    public void testLoadCoins() throws CashOverloadException, NullPointerSimulationException {
        dispenser.load(coin1Dollar, coin25Cents);
        assertEquals(2, dispenser.size());
    }
    
    @Test
    public void testUnloadCoins() throws SimulationException, CashOverloadException {
        dispenser.load(coin1Dollar, coin25Cents);
        List<Coin> unloadedCoins = dispenser.unload();
        assertEquals(2, unloadedCoins.size());
    }
    
    @Test
    public void testLoadNullCoin() {
        assertThrows(NullPointerSimulationException.class, () -> dispenser.load(coin1Dollar, null));
    }


}
