package com.thelocalmarketplace.hardware.test;

import org.junit.Before;
import org.junit.Test;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.Sink;
import com.tdc.coin.AbstractCoinValidator;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinValidator;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.NoPowerException;

import java.math.BigDecimal;
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
	
	
    @Before
    public void setup() 
    {
        validator = new CoinValidator(validCurrency,validDenominations);
    }

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
    
}




