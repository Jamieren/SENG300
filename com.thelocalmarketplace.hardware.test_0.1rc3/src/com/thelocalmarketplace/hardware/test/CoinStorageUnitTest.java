/*
 * SENG300 Assignment 3
 * Zhenhui Ren (30139966) 
 * Yang Yang (30156356)
 * Sukhnaaz Sidhu (30161587)
 * Ranbir Singh (30187921)
 */

package com.thelocalmarketplace.hardware.test;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.coin.AbstractCoinStorageUnit;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinStorageUnit;
import com.tdc.coin.CoinStorageUnitObserver;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;


public class CoinStorageUnitTest {

    private CoinStorageUnit unit;
    private Coin coin25Cents;  // USD 0.25
    private Coin coin1Dollar;  // USD 1.00
	private DummyStorageUnitObserver dummyObserver;

    public class DummyStorageUnitObserver implements CoinStorageUnitObserver {

        private boolean isCoinsFullCalled = false;
        private boolean isCoinAddedCalled = false;
        private boolean isCoinsLoadedCalled = false;
        private boolean isCoinsUnloadedCalled = false;

        @Override
        public void coinsFull(AbstractCoinStorageUnit unit) {
            isCoinsFullCalled = true;
        }

        @Override
        public void coinAdded(AbstractCoinStorageUnit unit) {
            isCoinAddedCalled = true;
        }

        @Override
        public void coinsLoaded(AbstractCoinStorageUnit unit) {
            isCoinsLoadedCalled = true;
        }

        @Override
        public void coinsUnloaded(AbstractCoinStorageUnit unit) {
            isCoinsUnloadedCalled = true;
        }

        // Getter methods to check if a method was called

        public boolean isCoinsFullCalled() {
            return isCoinsFullCalled;
        }

        public boolean isCoinAddedCalled() {
            return isCoinAddedCalled;
        }

        public boolean isCoinsLoadedCalled() {
            return isCoinsLoadedCalled;
        }

        public boolean isCoinsUnloadedCalled() {
            return isCoinsUnloadedCalled;
        }

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
    }
    
    
    @Before
    public void setUp() {
        unit = new CoinStorageUnit(10); // create a unit with a capacity of 10 coins
        //unit.activate(); //assume the unit is activated
        Coin.DEFAULT_CURRENCY = Currency.getInstance("USD");  // set default currency for Coin
        coin25Cents = new Coin(new BigDecimal("0.25")); // 25 cents coin
        coin1Dollar = new Coin(new BigDecimal("1.00")); // 1 dollar coin
        dummyObserver = new DummyStorageUnitObserver();
        unit.attach(dummyObserver); // Assuming there's an addObserver method
        //Turn on power
        unit.connect(PowerGrid.instance());
        unit.isConnected();
        unit.activate( );
        unit.isActivated( );
        unit.enable();
    }

    @Test
    public void testConstructor() {
        assertNotNull(unit);
    }

    @Test(expected = InvalidArgumentSimulationException.class)
    public void testNegativeCapacity() {
        new CoinStorageUnit(-1);
    }

    
    @Test
    public void testGetCapacity() {
        assertEquals(10, unit.getCapacity()); //we set up capacity as 10 before
    }

    @Test(expected = NoPowerException.class)
    public void testGetCoinCountNoPower() {
        unit.disactivate();  // Turn off power
        unit.getCoinCount();
    }

    @Test(expected = NoPowerException.class)
    public void testLoadNoPower() throws CashOverloadException {   
        unit.disactivate();  // Turn off power
        unit.load(coin25Cents, coin1Dollar);
    }

    @Test(expected = NoPowerException.class)
    public void testUnloadNoPower() {
        unit.disactivate();  // Turn off power
        unit.unload();
    }

    @Test
    public void testLoadAndUnloadCoins() {
        try {
            System.out.println("Before load, coin count: " + unit.getCoinCount());
            unit.load(coin25Cents, coin1Dollar);
            System.out.println("After load, coin count: " + unit.getCoinCount());
            
            assertEquals(2, unit.getCoinCount());	//FAIL
            
            unit.unload();
            System.out.println("After unload, coin count: " + unit.getCoinCount());
            
            assertEquals(0, unit.getCoinCount());	//FAIL
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoadTooManyCoins() {
        try {
            for (int i = 1; i <= 11; i++) {
                System.out.println("Loading coin number " + i);
                unit.load(coin25Cents);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    @Test(expected = NoPowerException.class)
    public void testReceiveCoinNoPower() throws CashOverloadException, DisabledException {
        unit.disactivate();  // Turn off power
        unit.receive(coin1Dollar);
    }
    
    @Test
    public void testUnloadWhenEmpty() {
        List<Coin> coins = unit.unload();
        assertTrue("Expected empty list after unloading an empty storage.", coins.isEmpty());//FAIL
    }

    
    @Test(expected = NoPowerException.class)
    public void testHasSpaceNoPower() 
    {
    	unit.disactivate();
    	unit.hasSpace();
    }
    
    @Test
    public void testHasSpaceHasPower() {
        assertTrue("Storage should have space initially.", unit.hasSpace());
        for (int i = 0; i < 10; i++) {
            try {
                unit.load(coin25Cents);
            } catch(Exception e) {
                fail("Exception thrown when loading coin: " + e.getMessage());
            }
        }
        assertFalse("Storage should not have space after loading 10 coins.", unit.hasSpace());
    }

    @Test(expected = CashOverloadException.class)
    public void testReceiveWhenFull() throws CashOverloadException, DisabledException {
        for (int i = 0; i < 10; i++) {
            unit.load(coin25Cents);
        }
        unit.receive(coin25Cents);
    }

    @Test
    public void testSingleReceive() {
        try {
            unit.receive(coin25Cents);
        } catch (Exception e) {
            e.printStackTrace();  // This will give a stack trace to help identify the problem.
            fail("Exception during single coin receive: " + e.getMessage());//FAIL
        }
    }


    @Test(expected = DisabledException.class)
    public void testReceiveWhenDisabled() throws CashOverloadException, DisabledException {
        unit.disable();
        unit.receive(coin1Dollar);  // This should throw DisabledException
    }

    @Test(expected = NoPowerException.class)
    public void testReceiveWhenNoPower() throws CashOverloadException, DisabledException {
        unit.disactivate();  // Turn off power
        unit.receive(coin1Dollar);  // This should throw NoPowerException
    }


    @Test(expected = NullPointerSimulationException.class)
    public void testReceiveNullCoin() throws CashOverloadException, DisabledException {
        unit.receive(null);  // This should throw NullPointerSimulationException
    }

    @Test
    public void testReceiveCoinSuccessfully() throws CashOverloadException, DisabledException {
        int initialCount = unit.getCoinCount();
        unit.receive(coin1Dollar);
        assertEquals("Coin count should increase by 1 after receiving a coin.", initialCount + 1, unit.getCoinCount());
    }

    @Test(expected = CashOverloadException.class)
    public void testReceiveWhenStorageFull() throws CashOverloadException, DisabledException {
        for (int i = 0; i < 10; i++) {
            unit.load(coin25Cents);
        }
        unit.receive(coin1Dollar);  // This should throw CashOverloadException
    }

    @Test(expected = CashOverloadException.class)
    public void testLoadBeyondCapacity() throws CashOverloadException {
        Coin[] coins = new Coin[11];
        Arrays.fill(coins, coin25Cents);
        unit.load(coins);  // This should throw CashOverloadException
    }

    @Test(expected = NullPointerSimulationException.class)
    public void testLoadNullCoin() throws CashOverloadException {
        unit.load(coin25Cents, null, coin1Dollar);  // This should throw NullPointerSimulationException
    }

    @Test
    public void testActivation() {
        unit.disactivate();
        assertFalse(unit.isActivated());
        unit.activate();
        assertTrue(unit.isActivated());
    }
    
    @Test(expected = NoPowerException.class)
    public void testLoadCoinNoPower() throws CashOverloadException {
        unit.disactivate();  // Turn off power
        unit.load(coin1Dollar);  // Trying to load a coin without power
    }
    
    @Test(expected = NoPowerException.class)
    public void testUnloadCoinsNoPower() {
        unit.disactivate();  // Turn off power
        unit.unload();  // Trying to unload coins without power
    }
    
    @Test
    public void testLoadCoins() throws CashOverloadException, NoPowerException {
        unit.load(coin25Cents);
        assertTrue(dummyObserver.isCoinsLoadedCalled());
    }

    @Test
    public void testUnloadCoins() throws NoPowerException {
        unit.unload();
        assertTrue(dummyObserver.isCoinsUnloadedCalled());
    }

    @Test
    public void testReceiveCoin() throws CashOverloadException, DisabledException, NoPowerException {
        // Fill the unit to its capacity first
        for (int i = 0; i < unit.getCapacity() - 1; i++) {
            unit.load(coin25Cents);
        }
        unit.receive(coin25Cents);
        assertTrue(dummyObserver.isCoinsFullCalled());
    }

    @Test
    public void testAddCoin() throws CashOverloadException, DisabledException, NoPowerException {
        unit.receive(coin25Cents);
        assertTrue(dummyObserver.isCoinsLoadedCalled());
    }
    

}