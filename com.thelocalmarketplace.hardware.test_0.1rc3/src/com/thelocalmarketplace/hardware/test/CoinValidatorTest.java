/*
 * SENG300 Assignment 3
 * Zhenhui Ren (30139966) 
 * Yang Yang (30156356)
 * Sukhnaaz Sidhu (30161587)
 * Ranbir Singh (30187921)
 */

package com.thelocalmarketplace.hardware.test;

import org.junit.Before;
import org.junit.Test;

import com.tdc.CashOverloadException;
import com.tdc.ComponentFailure;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.Sink;
import com.tdc.coin.AbstractCoinValidator;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.CoinValidatorObserver;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CoinValidatorTest {
	
	private AbstractCoinValidator validator;
	private Currency validCurrency = Currency.getInstance("USD");;
	private List<BigDecimal> validDenominations = Arrays.asList(new BigDecimal("0.05"), new BigDecimal("0.10"));
	private BigDecimal invalidDenomination = new BigDecimal("0.03");
	
	/**
     * DummySink is a dumb stub implementation of the Sink<Coin> interface.
     * It's used within this test class to simulate the behavior of a real Sink without 
     * introducing complex logic.
     */
    private class DummySink implements Sink<Coin> {
        private List<Coin> receivedCoins = new ArrayList<>();

        @Override
        public void receive(Coin coin)  
        {
            receivedCoins.add(coin);
        }

        @Override
        public boolean hasSpace() 
        {
            return true;
        }
        
        public boolean receivedCoin(Coin coin) 
        {
            return receivedCoins.contains(coin);
        }
    }
    
    /**
     * DummyCoinValidatorObserver is a dumb stub implementation of the CoinValidatorObserver interface.
     * It's used within this test class to simulate the behavior of a real CoinValidatorObserver without 
     * introducing complex logic.
     */

    public class DummyCoinValidatorObserver implements CoinValidatorObserver 
    {
        public boolean wasValidCalled = false;
        public boolean wasInvalidCalled = false;

        
        @Override
        public void validCoinDetected(AbstractCoinValidator validator, BigDecimal value) {
            wasValidCalled = true;
        }

        @Override
        public void invalidCoinDetected(AbstractCoinValidator validator) {
            wasInvalidCalled = true;
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
    public void setup() 
    {
        validator = new CoinValidator(validCurrency,validDenominations);
        //Turn on power
    	validator.connect(PowerGrid.instance());
    	validator.isConnected();
    	validator.activate( );
    	validator.isActivated( );
    	validator.enable();
    }
    
    
    
/*************
 * CoinValidator testing part
 */
    
//Test when currency is null
    @Test(expected = NullPointerSimulationException.class)
    public void testNullCurrency() 
    {
        new CoinValidator(null, validDenominations);
    }

//Test when coinDenominations is null
    @Test(expected = NullPointerSimulationException.class)
    public void testNullDenominations() 
    {
        new CoinValidator(validCurrency, null);
    }

//Test when coinDomininations contains no denominations
    @Test(expected = InvalidArgumentSimulationException.class)
    public void testEmptyDenominations() 
    {
        new CoinValidator(validCurrency, Arrays.asList());
    }

//Test when one denomination in coinDenominations is null
    @Test(expected = NullPointerSimulationException.class)
    public void testDenominationsWithNull() 
    {
        new CoinValidator(validCurrency, Arrays.asList(new BigDecimal("0.05"), null));
    }

//Test when one denomination in coinDenominations is non-positive
    @Test(expected = InvalidArgumentSimulationException.class)
    public void testNonPositiveDenominations() 
    {
        new CoinValidator(validCurrency, Arrays.asList(new BigDecimal("0.05"), BigDecimal.ZERO));
    }

//Test when one denomination in coinDenominations is repeated    
    @Test(expected = InvalidArgumentSimulationException.class)
    public void testNonUniqueDenominations() 
    {
        new CoinValidator(validCurrency, Arrays.asList(new BigDecimal("0.05"), new BigDecimal("0.05")));
    }

//Test when the parameters are valid    
    @Test
    public void testValidConstructor() 
    {
        new CoinValidator(validCurrency, validDenominations);
    }
    
    
/*************
* AbostractCoinValidator testing part
*/
  //1. Testing the setup(...) method:
  //Test with various combinations of null arguments.
      @Test(expected = NullPointerSimulationException.class)
      public void testSetupWithNullRejectionSink() 
      {
          validator.setup(null, new HashMap<>(), new DummySink());
      }

      @Test(expected = NullPointerSimulationException.class)
      public void testSetupWithNullStandardSinks() 
      {
          validator.setup(new DummySink(), null, new DummySink());
      }

      @Test(expected = NullPointerSimulationException.class)
      public void testSetupWithNullOverflowSink() 
      {
          validator.setup(new DummySink(), new HashMap<>(), null);
      }
      
      
    //Test with null standard sinks and denominations.
      @Test(expected = NullPointerSimulationException.class)
      public void testSetupWithNullStandardSinksAndDenominations() 
      {
          Map<BigDecimal, Sink<Coin>> standardSinks = new HashMap<>();
          standardSinks.put(validDenominations.get(0), null);
          standardSinks.put(validDenominations.get(1), null);
          validator.setup(new DummySink(), standardSinks, new DummySink());
      }
      
  //Test with different numbers of standard sinks and null denominations.
      @Test(expected = InvalidArgumentSimulationException.class)
      public void testSetupWithMismatchedStandardSinksAndDenominations() 
      {
          Map<BigDecimal, Sink<Coin>> standardSinks = new HashMap<>();
          standardSinks.put(validDenominations.get(0), new DummySink());
          // Only one sink provided, but two denominations exist in the validator
          validator.setup(new DummySink(), standardSinks, new DummySink());
      }
      
  //Test with repeated standard sinks.
      @Test(expected = InvalidArgumentSimulationException.class)
      public void testSetupWithRepeatedStandardSinks() 
      {
          DummySink repeatedSink = new DummySink();
          Map<BigDecimal, Sink<Coin>> standardSinks = new HashMap<>();
          standardSinks.put(validDenominations.get(0), repeatedSink);
          standardSinks.put(validDenominations.get(1), repeatedSink); // Using the same sink for both denominations
          validator.setup(new DummySink(), standardSinks, new DummySink()); // Using the same sink as rejection and overflow as well
      }

  //Test with repeated rejection sinks.
      @Test(expected = InvalidArgumentSimulationException.class)
      public void testRejectionSinkInStandardSinks() 
      {
          DummySink rejectionDummySink = new DummySink();
          Map<BigDecimal, Sink<Coin>> standardSinks = new HashMap<>();
          //Use rejection sink in standard sinks
          standardSinks.put(validDenominations.get(0), rejectionDummySink);
          //standardSinks.put(validDenominations.get(1), rejectionSink); 
          validator.setup(rejectionDummySink, standardSinks, new DummySink()); 
      }

  //Test with repeated overflow sinks.
      @Test(expected = InvalidArgumentSimulationException.class)
      public void testOverflowSinkInStandardSinks() 
      {
          DummySink overflowDummySink = new DummySink();
          Map<BigDecimal, Sink<Coin>> standardSinks = new HashMap<>();
          //Use overflow sink in standard sinks
          standardSinks.put(validDenominations.get(0), overflowDummySink);
          //standardSinks.put(validDenominations.get(1), overflowSink); 
          validator.setup(new DummySink(), standardSinks, overflowDummySink); 
      }
      
  //Test with valid setup configurations.
      @Test
      public void testValidSetup() 
      {
      	DummySink rejectionSink = new DummySink();
      	DummySink overflowSink = new DummySink();
          Map<BigDecimal, Sink<Coin>> standardSinks = new HashMap<>();
          standardSinks.put(new BigDecimal("0.05"), new DummySink());
          standardSinks.put(new BigDecimal("0.10"), new DummySink());
          validator.setup(rejectionSink, standardSinks, overflowSink); 
      }


  //2. Testing the isValid() method:
  //Test with coins of valid currencies.
      @Test
      public void testIsValidWithValidCurrency() 
      {
          Coin validCoin = new Coin(validCurrency, validDenominations.get(0));
          DummySink rejectionDummySink = new DummySink();
          Map<BigDecimal, Sink<Coin>> standardSinks = new HashMap<>();
          standardSinks.put(validDenominations.get(0), new DummySink());
          standardSinks.put(validDenominations.get(1), new DummySink());
          validator.setup(rejectionDummySink, standardSinks, new DummySink());
          
          try {
              validator.receive(validCoin);
          } catch (Exception e) {
              // Ignoring exceptions for this test
          }

          assertFalse("Expected coin not to be sent to rejection sink due to valid currency.",
                     rejectionDummySink.receivedCoin(validCoin));
      }

  //Test with coins of invalid currencies.
      @Test
      public void testIsValidWithInvalidCurrency() 
      {
      	//Use CAD as the wrong currency
          Coin invalidCurrencyCoin = new Coin(Currency.getInstance("CAD"), validDenominations.get(0));
          DummySink rejectionDummySink = new DummySink();
          Map<BigDecimal, Sink<Coin>> standardSinks = new HashMap<>();
          standardSinks.put(validDenominations.get(0), new DummySink());
          standardSinks.put(validDenominations.get(1), new DummySink());
          validator.setup(rejectionDummySink, standardSinks, new DummySink());

          try {
              validator.receive(invalidCurrencyCoin);
          } catch (Exception e) {
              // Ignoring exceptions for this test
          }

          assertTrue("Expected coin to be sent to rejection sink due to invalid currency.",
                     rejectionDummySink.receivedCoin(invalidCurrencyCoin));
          
      }
      
  //Test with coins of valid denominations.
      @Test
      public void testIsValidWithValidDenomination() 
      {
          Coin validDenominationCoin = new Coin(validCurrency, validDenominations.get(0));
          DummySink rejectionDummySink = new DummySink();
          Map<BigDecimal, Sink<Coin>> standardSinks = new HashMap<>();
          standardSinks.put(validDenominations.get(0), new DummySink());
          standardSinks.put(validDenominations.get(1), new DummySink());
          validator.setup(rejectionDummySink, standardSinks, new DummySink());

          try {
              validator.receive(validDenominationCoin);
          } catch (Exception e) {
              // Ignoring exceptions for this test
          }

          assertFalse("Expected coin not to be sent to rejection sink due to valid denomination.",
                     rejectionDummySink.receivedCoin(validDenominationCoin));
      }

      
  //Test with coins of invalid denominations.
      public void testIsValidWithInvalidDenomination() 
      {
          Coin invalidDenominationCoin = new Coin(validCurrency, invalidDenomination);
          DummySink rejectionDummySink = new DummySink();
          Map<BigDecimal, Sink<Coin>> standardSinks = new HashMap<>();
          standardSinks.put(validDenominations.get(0), new DummySink());
          validator.setup(rejectionDummySink, standardSinks, new DummySink());

          try {
              validator.receive(invalidDenominationCoin);
          } catch (Exception e) {
              // Ignoring exceptions for this test
          }

          assertTrue("Expected coin to be sent to rejection sink due to invalid denomination.",
                     rejectionDummySink.receivedCoin(invalidDenominationCoin));
      }

      
  //3. Testing the receive() method:
  //Test the method when the validator is disactivated.
      @Test(expected = NoPowerException.class)
      public void testReceiveWhenNotActivated() throws DisabledException, CashOverloadException 
      {
          validator.disactivate();
          Coin validCoin = new Coin(validCurrency, validDenominations.get(0));
          validator.receive(validCoin);  
      }

  // Test the method when the validator is disabled.
      @Test(expected = DisabledException.class)
      public void testReceiveWhenDisabled() throws DisabledException, CashOverloadException 
      {
          validator.disable();
          Coin validCoin = new Coin(validCurrency, validDenominations.get(0));
          validator.receive(validCoin); 
      }
      
  //Test with a null coin
      @Test(expected = NullPointerSimulationException.class)
      public void testReceiveWithNullCoin() 
      {
      	Coin nullCoin = null; 
          try {
  			validator.receive(nullCoin);
  		} catch (DisabledException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} catch (CashOverloadException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
      }
      
      
  // Test with a coin of the wrong currency.
      @Test
      public void testReceiveWithWrongCurrency() 
      {
      	//Use CAD as the wrong currency
          Coin wrongCurrencyCoin = new Coin(Currency.getInstance("CAD"), validDenominations.get(0)); 
          DummySink rejectionDummySink = new DummySink();

          // Setup standard sinks for each denomination
          HashMap<BigDecimal, Sink<Coin>> standardSinks = new HashMap<>();
          for (BigDecimal denomination : validDenominations) 
          {
              standardSinks.put(denomination, new DummySink());
          }

          validator.setup(rejectionDummySink, standardSinks, new DummySink());

          try {
  			validator.receive(wrongCurrencyCoin);
  		} catch (DisabledException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} catch (CashOverloadException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}

          assertTrue("Expected coin to be sent to rejection sink due to wrong currency.",
                     rejectionDummySink.receivedCoin(wrongCurrencyCoin));
      }

      // Test with a coin of the right currency but wrong denomination.
      @Test
      public void testReceiveWithWrongDenomination() 
      {
          Coin wrongDenominationCoin = new Coin(validCurrency, invalidDenomination);
          DummySink rejectionDummySink = new DummySink();

          // Setup standard sinks for each denomination
          HashMap<BigDecimal, Sink<Coin>> standardSinks = new HashMap<>();
          for (BigDecimal denomination : validDenominations) 
          {
              standardSinks.put(denomination, new DummySink());
          }

          validator.setup(rejectionDummySink, standardSinks, new DummySink());

          try {
              validator.receive(wrongDenominationCoin);
          } catch (Exception e) {
              // Ignoring expected exceptions for this test
          }
          
          assertTrue("Expected coin to be sent to rejection sink due to wrong denomination.",
                     rejectionDummySink.receivedCoin(wrongDenominationCoin));
      }

      // Test with valid coins where the sink has space.
      @Test
      public void testReceiveWithSpaceInSink() {
          Coin validCoin = new Coin(validCurrency, validDenominations.get(0));
          DummySink standardDummySink = new DummySink();
          HashMap<BigDecimal, Sink<Coin>> standardSinks = new HashMap<>();
          standardSinks.put(validDenominations.get(0), standardDummySink);
          standardSinks.put(validDenominations.get(1), new DummySink());

          validator.setup(new DummySink(), standardSinks, new DummySink());


          try {
  			validator.receive(validCoin);
  		} catch (DisabledException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} catch (CashOverloadException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}

          assertTrue("Expected coin to be sent to the standard sink.",
                     standardDummySink.receivedCoin(validCoin));
      }

      // Test with valid coins where the sink does not have space (overflow).
      @Test
      public void testReceiveWithOverflow() 
      {
          Coin validCoin = new Coin(validCurrency, validDenominations.get(0));
          DummySink standardDummySink = new DummySink() 
          {
              @Override
              public boolean hasSpace() 
              {
                  return false;
              }
          };
          HashMap<BigDecimal, Sink<Coin>> standardSinks = new HashMap<>();
          standardSinks.put(validDenominations.get(0), standardDummySink);
          standardSinks.put(validDenominations.get(1), new DummySink());
          
          DummySink overflowDummySink = new DummySink();
          validator.setup(new DummySink(), standardSinks, overflowDummySink);

          try {
              validator.receive(validCoin);
          } catch (Exception e) {
              // Ignoring expected exceptions for this test
          }

          assertTrue("Expected coin to be sent to the overflow sink due to standard sink being full.",
                     overflowDummySink.receivedCoin(validCoin));
      }
      
      
  //4. Testing the hasSpace() method:
  //Test this method to ensure it always returns true when there's power.
      @Test
      public void testAvtivateHasSpace() 
      {
      	validator.activate();
          assertTrue("Expected hasSpace to always return true.", validator.hasSpace());
      }
      
  //Test this method will throw NoPowerException when there's no power..
      @Test(expected = NoPowerException.class)
      public void testDisavtivateHasSpace() 
      {
      	validator.disconnect();
          validator.hasSpace();
      }


  //5. Testing the reject() method:
  //Test this method will throw ComponentFailure when there's power.
      @Test(expected = ComponentFailure.class)
      public void testRejectActivateThrowsException() 
      {
      	Coin validCoin = new Coin(validCurrency, new BigDecimal("0.05"));
          validator.reject(validCoin);
      }
      
  //Test this method will throw NoPowerException when there's no power.
      @Test(expected = NoPowerException.class)
      public void testRejectDisactivateThrowsException() 
      {
      	Coin validCoin = new Coin(validCurrency, new BigDecimal("0.05"));
      	validator.disconnect();
      	validator.reject(validCoin);
      }


 /*************
  * CoinValidatorObserver testing part
 */
 //Ensure that when a valid coin is detected, the validCoinDetected observer method is called.
      @Test
      public void testValidCoinNotification() 
      {
          DummyCoinValidatorObserver observer = new DummyCoinValidatorObserver();
          validator.attach(observer);
          Coin validCoin = new Coin(validCurrency, validDenominations.get(0)); // Assuming you've initialized validDenominations

          try {
              validator.receive(validCoin);
          } catch (Exception e) {
              // Handle or ignore exceptions for the sake of this test
          }

          assertTrue("Expected validCoinDetected to be called on observer.", observer.wasValidCalled);
      }
    
  //Ensure that when an invalid coin is detected, the invalidCoinDetected observer method is called.
      @Test
      public void testInvalidCoinNotification() 
      {
          DummyCoinValidatorObserver observer = new DummyCoinValidatorObserver();
          validator.attach(observer);
          Coin invalidCoin = new Coin(validCurrency, invalidDenomination); 

          try {
              validator.receive(invalidCoin);
          } catch (Exception e) {
              // Handle or ignore exceptions for the sake of this test
          }

          assertTrue("Expected invalidCoinDetected to be called on observer.", observer.wasInvalidCalled);
      }
    
}




