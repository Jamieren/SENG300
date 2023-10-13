/*
 * SENG300 Assignment 3
 * Zhenhui Ren (30139966) 
 * Yang Yang (30156356)
 * Sukhnaaz Sidhu (30161587)
 * Ranbir Singh (30187921)
 */

package com.thelocalmarketplace.hardware.test;


import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.tdc.CashOverloadException;
import com.tdc.ComponentFailure;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.NoCashAvailableException;
import com.tdc.PassiveSource;
import com.tdc.Sink;
import com.tdc.coin.AbstractCoinDispenser;
import com.tdc.coin.AbstractCoinValidator;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinDispenser;
import com.tdc.coin.CoinDispenserObserver;
import com.tdc.coin.CoinValidatorObserver;
import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.hardware.test.AbstractCoinValidatorTest.DummyCoinValidatorObserver;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;


public class CoinDispenserTest {

    private CoinDispenser dispenser;
    private Coin coin25Cents;  // USD 0.25
    private Coin coin1Dollar;  // USD 1.00
    private CoinDispenserObserver observer;



    @Before
    public void setUp() {
        dispenser = new CoinDispenser(10);
        dispenser.connect(PowerGrid.instance()); 				// connect to power grid
        dispenser.activate(); 									// assume the unit is activated
        Coin.DEFAULT_CURRENCY = Currency.getInstance("USD");  	// set default currency for Coin
        coin25Cents = new Coin(new BigDecimal("0.25")); 		// 25 cents coin
        coin1Dollar = new Coin(new BigDecimal("1.00")); 		// 1 dollar coin
    }

    @Test
    public void testReceiveValidCoin() throws CashOverloadException, DisabledException {
        dispenser.receive(coin1Dollar); // test by receiving 1 $1 USD coin 
        assertTrue(dispenser.hasSpace());
    }
    
    @Test
    public void testReceiveWhenFull() throws CashOverloadException, DisabledException {
        int capacity = dispenser.getCapacity();
        for (int i = 0; i < capacity; i++) {
            dispenser.receive(coin1Dollar);
        }
        assertThrows(CashOverloadException.class, () -> dispenser.receive(coin1Dollar)); // check to see if throws OverloadException when full
    }

    @Test(expected = SimulationException.class)
    public void testNegativeCapacity() {
        new CoinDispenser(-10);  // check to see if negative size dispenser is allowed
    }

    @Test(expected = SimulationException.class)
    public void testZeroCapacity() {
        new CoinDispenser(0); // check zero capacity
    }

    @Test 
    public void testHasSpaceInitial() {
        assertTrue(dispenser.hasSpace()); // check to see if has space before accepting coins
    }

    @Test(expected = SimulationException.class)
    public void testReceiveNullCoin() throws Exception {
        dispenser.receive(null); // check to see if null coin is accepted
    }

    @Test(expected = DisabledException.class)
    public void testReceiveWhenDisabled() throws CashOverloadException, DisabledException {
        dispenser.disable(); // Simulate a disabled dispenser
        dispenser.receive(coin1Dollar);
    }
    
    @Test
    public void testLoadCoins() throws CashOverloadException, NullPointerSimulationException {
        dispenser.load(coin1Dollar, coin25Cents);
        assertEquals(2, dispenser.size()); // is size 2 after adding 2 coins
    }

    @Test(expected = CashOverloadException.class)
    public void testOverloadLoadCoins() throws CashOverloadException, NullPointerSimulationException {
        dispenser.load(coin1Dollar, coin25Cents, coin25Cents, coin25Cents, coin25Cents, coin25Cents, coin25Cents, coin25Cents, coin25Cents, coin25Cents, coin25Cents); // load 11 coins
    }
    
    @Test
    public void testUnload() throws NoPowerException, SimulationException, CashOverloadException {
        dispenser.load(coin1Dollar , coin1Dollar , coin1Dollar); // Load 3 coins
        int originalSize = dispenser.size();

        dispenser.unload(); // Unload coins
        assertEquals(originalSize, dispenser.getCapacity());
    }
    
    @Test
    public void testUnloadCoins() throws SimulationException, CashOverloadException {
        dispenser.load(coin1Dollar, coin25Cents);
        List<Coin> unloadedCoins = dispenser.unload(); // unload coins into list
        assertEquals(2, unloadedCoins.size());
    }
    
    @Test(expected = NullPointerSimulationException.class)
    public void testLoadNullCoin() throws SimulationException, CashOverloadException {
        dispenser.load(coin1Dollar, null); // check if null coin can be loaded
    }
    
    // Check result if disabled
    @Test(expected = NoPowerException.class)
    public void testLoadDisactivated() throws SimulationException, CashOverloadException {
    	dispenser.disactivate();
    	dispenser.load(coin1Dollar, coin25Cents);
    }
    
    @Test(expected = NoPowerException.class)
    public void testUnloadDisactivated() throws SimulationException, CashOverloadException {
    	dispenser.load(coin1Dollar);
    	dispenser.disactivate();
    	dispenser.unload();
    }
    
    @Test(expected = NoPowerException.class)
    public void testReceiveDisactivated() throws SimulationException, CashOverloadException, DisabledException {
    	dispenser.load(coin1Dollar);
    	dispenser.disactivate();
    	dispenser.receive(coin1Dollar);
    }
    
    @Test(expected = NoPowerException.class)
    public void testEmitDisactivated() throws SimulationException, CashOverloadException, NoCashAvailableException, DisabledException {
    	dispenser.load(coin1Dollar);
    	dispenser.disactivate();
    	dispenser.emit();
    }
    
    @Test(expected = NoPowerException.class)
    public void testHasSpaceDisactivated() throws SimulationException, CashOverloadException   {
    	dispenser.load(coin1Dollar);
    	dispenser.disactivate();
    	dispenser.hasSpace();
    }
    
    @Test(expected = NoPowerException.class)
    public void testRejectDisactivated() throws DisabledException, CashOverloadException  {
    	dispenser.load(coin1Dollar);
    	dispenser.disactivate();
    	dispenser.reject(coin1Dollar);
    }
    
    @Test
    public void testEmit() throws NoPowerException, DisabledException, CashOverloadException, NoCashAvailableException {
        Sink<Coin> coinSinkStub = new Sink<Coin>() { // sink stub

            @Override
            public void receive(Coin cash) throws CashOverloadException, DisabledException {
            }

            @Override
            public boolean hasSpace() {
                return true;
            }

        };
        dispenser.sink = coinSinkStub; // setup new sink

        dispenser.load(coin1Dollar, coin1Dollar, coin1Dollar); // Load 3 $1 coins
        int originalSize = dispenser.size();

        dispenser.emit();
        assertEquals(originalSize - 1, dispenser.size()); // check size decreased by 1
    }
    
    @Test(expected = DisabledException.class)
    public void testEmitDisabled() throws NoCashAvailableException, CashOverloadException, DisabledException {
        dispenser.disable();
        dispenser.emit();
    }
    
    @Test(expected = NoCashAvailableException.class)
    public void testEmitNoCashAvailable() throws CashOverloadException, NoCashAvailableException, DisabledException  {
        dispenser.emit();
    }
    
    @Test
    public void testReject() throws DisabledException, ComponentFailure, CashOverloadException {
        PassiveSource<Coin> cashSourceStub = new PassiveSource<Coin>() { // stub
            
			@Override
			public void reject(Coin cash) throws CashOverloadException, DisabledException, ComponentFailure {
				// TODO Auto-generated method stub
			}
        };

        dispenser.source = cashSourceStub;
        dispenser.reject(coin1Dollar);
        // Assert behavior here
    }
    
    @Test(expected = NoPowerException.class)
    public void testRejectNoPower() throws DisabledException, CashOverloadException, NoPowerException {
    	PassiveSource<Coin> cashSourceStub = new PassiveSource<Coin>() {
            
			@Override
			public void reject(Coin cash) throws CashOverloadException, DisabledException, ComponentFailure {
			}
        };

        dispenser.source = cashSourceStub;
        dispenser.disactivate();
        dispenser.reject(coin1Dollar);
    }
    
    // stub implementation
    public class DummyCoinDispenserObserver implements CoinDispenserObserver 
    {

		@Override
		public void enabled(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void disabled(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void turnedOn(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void turnedOff(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void coinsFull(ICoinDispenser dispenser) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void coinsEmpty(ICoinDispenser dispenser) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void coinAdded(ICoinDispenser dispenser, Coin coin) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void coinRemoved(ICoinDispenser dispenser, Coin coin) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void coinsLoaded(ICoinDispenser dispenser, Coin... coins) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void coinsUnloaded(ICoinDispenser dispenser, Coin... coins) {
			// TODO Auto-generated method stub
			
		}
        
    }

    


}
