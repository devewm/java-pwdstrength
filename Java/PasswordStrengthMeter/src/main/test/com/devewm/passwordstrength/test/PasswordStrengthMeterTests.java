package com.devewm.passwordstrength.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Test;

import com.devewm.passwordstrength.PasswordStrengthClass;
import com.devewm.passwordstrength.PasswordStrengthMeter;


public class PasswordStrengthMeterTests {
	
	@Test
	public void testCodePoints() {
		assertEquals((int) ' ', 32);
		assertEquals((int) 'A', 65);
		assertEquals((int) 'a', 97);
		assertEquals((int) '¡', 161);
		
	}
	
	@Test
	public void singleLetterPasswords() {
		String password;
		BigInteger result;
		
		password = "a";
		result = PasswordStrengthMeter.check(password, false);
		assertEquals(new BigInteger("1"), result);
		
		password = "b";
		result = PasswordStrengthMeter.check(password, false);
		assertEquals(new BigInteger("2"), result);
		
		password = "z";
		result = PasswordStrengthMeter.check(password, false);
		assertEquals(new BigInteger("26"), result);
		
		password = "0";
		result = PasswordStrengthMeter.check(password, false);
		assertEquals(new BigInteger("1"), result);
		
		password = "3";
		result = PasswordStrengthMeter.check(password, false);
		assertEquals(new BigInteger("4"), result);
		
	}
	
	@Test
	public void twoLetterPasswords() {
		String password;
		BigInteger result;
		
		password = "aa";
		result = PasswordStrengthMeter.check(password, false);
		assertEquals(new BigInteger("27"), result);
		
		password = "ab";
		result = PasswordStrengthMeter.check(password, false);
		assertEquals(new BigInteger("28"), result);
		
		password = "ba";
		result = PasswordStrengthMeter.check(password, false);
		assertEquals(new BigInteger("53"), result);
	}
	
	@Test
	public void threeLetterPasswords() {
		String password;
		BigInteger result;
		
		// 26^2 + 26^1 + 1
		password = "aaa";
		result = PasswordStrengthMeter.check(password, false);
		assertEquals(new BigInteger("703"), result);
		
		// (36^2 + 36^1) + 26*(36^0) + 1 = 1359
		password = "AAA";
		result = PasswordStrengthMeter.check(password, false);
		assertEquals(new BigInteger("703"), result);
		
		// (26^2 + 26^1) + 2
		password = "aab";
		result = PasswordStrengthMeter.check(password, false);
		assertEquals(new BigInteger("704"), result);
		
		// (26^2 + 26^1) + 1*(26^2) + 1 = 1379
		password = "baa";
		result = PasswordStrengthMeter.check(password, false);
		assertEquals(new BigInteger("1379"), result);
		
		// (36^2 + 36^1) + 26*(36^0) + 1 = 1359
		password = "aa0";
		result = PasswordStrengthMeter.check(password, false);
		assertEquals(new BigInteger("1359"), result);
		
		// (62^2 + 62^1) + 26*(62^1) + 52*(62^0) + 1 = 5571
		password = "aA0";
		result = PasswordStrengthMeter.check(password, false);
		assertEquals(new BigInteger("5571"), result);
		
		// (62^2 + 62^1) + 25*(62^2) + 27*(62^1) + 52*(62^0) + 1 = 101733
		password = "zB0";
		result = PasswordStrengthMeter.check(password, false);
		assertEquals(new BigInteger("101733"), result);
		
	}
	
	@Test
	public void testQuickBrownFox() {
		String fox = "thequickbrownfoxjumpsoverthelazydog";
		
		BigInteger previousResult = null;
		for(int i = 1; i <= fox.length(); i++) {
			BigInteger result = PasswordStrengthMeter.check(fox.substring(0,i));
			if(null != previousResult) {
				assertTrue("Adding a letter results in at least 10 times the number of iterations", 
						result.compareTo(previousResult.multiply(new BigInteger("10"))) > 0);
			}
			
			previousResult = result;
		}
	}
	
	@Test
	public void testBigIntegerSizeLimit() {
		Random rand = new Random();
		StringBuffer password = new StringBuffer("");
		for(int i = 0; i < PasswordStrengthMeter.PASSWORD_LENGTH_LIMIT; i++) {
			password.append((char) rand.nextInt(255));
			PasswordStrengthMeter.check(password.toString(), false);
		}
		
		Exception exception = null;
		try {
			password.append("a");
			PasswordStrengthMeter.check(password.toString(), false);
		} catch(Exception ex) {
			exception = ex;
		}
		assertNotNull(exception);
		
		PasswordStrengthMeter.check(password.toString(), true);
	}
	
	@Test
	public void testStrengthClassifications() {
		String password;
		BigInteger result;
		
		// 8 characters, all lower case
		password = "aaaaaaaa";
		assertTrue(password.length() == 8);
		result = PasswordStrengthMeter.check(password, false);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_8_LOWER_CASE.getIterations()) >= 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_8_MIXED_CASE.getIterations()) < 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_8_MIXED_CASE_WITH_NUMBER.getIterations()) < 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_8_MIXED_CASE_WITH_NUMBER_AND_SYMBOL.getIterations()) < 0);
		
		// 8 characters mixed case
		password = "AAAAAAAa";
		assertTrue(password.length() == 8);
		result = PasswordStrengthMeter.check(password, false);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_8_LOWER_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_8_MIXED_CASE.getIterations()) >= 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_8_MIXED_CASE_WITH_NUMBER.getIterations()) < 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_8_MIXED_CASE_WITH_NUMBER_AND_SYMBOL.getIterations()) < 0);
		
		// 8 characters mixed case plus a number
		password = "000000Aa";
		assertTrue(password.length() == 8);
		result = PasswordStrengthMeter.check(password, false);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_8_LOWER_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_8_MIXED_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_8_MIXED_CASE_WITH_NUMBER.getIterations()) >= 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_8_MIXED_CASE_WITH_NUMBER_AND_SYMBOL.getIterations()) < 0);
		
		// 8 characters mixed case plus a number and a non-alphanumeric symbol
		password = "!!!!!Aa0";
		assertTrue(password.length() == 8);
		result = PasswordStrengthMeter.check(password, false);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_8_LOWER_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_8_MIXED_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_8_MIXED_CASE_WITH_NUMBER.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_8_MIXED_CASE_WITH_NUMBER_AND_SYMBOL.getIterations()) >= 0);
		
		// 10 characters, all lower case
		password = "aaaaaaaaaa";
		assertTrue(password.length() == 10);
		result = PasswordStrengthMeter.check(password, false);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_10_LOWER_CASE.getIterations()) >= 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_10_MIXED_CASE.getIterations()) < 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_10_MIXED_CASE_WITH_NUMBER.getIterations()) < 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_10_MIXED_CASE_WITH_NUMBER_AND_SYMBOL.getIterations()) < 0);
		
		// 10 characters mixed case
		password = "AAAAAAAAAa";
		assertTrue(password.length() == 10);
		result = PasswordStrengthMeter.check(password, false);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_10_LOWER_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_10_MIXED_CASE.getIterations()) >= 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_10_MIXED_CASE_WITH_NUMBER.getIterations()) < 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_10_MIXED_CASE_WITH_NUMBER_AND_SYMBOL.getIterations()) < 0);
		
		// 10 characters mixed case plus a number
		password = "00000000Aa";
		assertTrue(password.length() == 10);
		result = PasswordStrengthMeter.check(password, false);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_10_LOWER_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_10_MIXED_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_10_MIXED_CASE_WITH_NUMBER.getIterations()) >= 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_10_MIXED_CASE_WITH_NUMBER_AND_SYMBOL.getIterations()) < 0);
		
		// 10 characters mixed case plus a number and a non-alphanumeric symbol
		password = "!!!!!!!0Aa";
		assertTrue(password.length() == 10);
		result = PasswordStrengthMeter.check(password, false);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_10_LOWER_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_10_MIXED_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_10_MIXED_CASE_WITH_NUMBER.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_10_MIXED_CASE_WITH_NUMBER_AND_SYMBOL.getIterations()) >= 0);
		
		// 12 characters, all lower case
		password = "aaaaaaaaaaaa";
		assertTrue(password.length() == 12);
		result = PasswordStrengthMeter.check(password, false);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_12_LOWER_CASE.getIterations()) >= 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_12_MIXED_CASE.getIterations()) < 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_12_MIXED_CASE_WITH_NUMBER.getIterations()) < 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_12_MIXED_CASE_WITH_NUMBER_AND_SYMBOL.getIterations()) < 0);
		
		// 12 characters mixed case
		password = "AAAAAAAAAAAa";
		assertTrue(password.length() == 12);
		result = PasswordStrengthMeter.check(password, false);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_12_LOWER_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_12_MIXED_CASE.getIterations()) >= 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_12_MIXED_CASE_WITH_NUMBER.getIterations()) < 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_12_MIXED_CASE_WITH_NUMBER_AND_SYMBOL.getIterations()) < 0);
		
		// 12 characters mixed case plus a number
		password = "0000000000Aa";
		assertTrue(password.length() == 12);
		result = PasswordStrengthMeter.check(password, false);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_12_LOWER_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_12_MIXED_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_12_MIXED_CASE_WITH_NUMBER.getIterations()) >= 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_12_MIXED_CASE_WITH_NUMBER_AND_SYMBOL.getIterations()) < 0);
		
		// 12 characters mixed case plus a number and a non-alphanumeric symbol
		password = "!!!!!!!!!0Aa";
		assertTrue(password.length() == 12);
		result = PasswordStrengthMeter.check(password, false);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_12_LOWER_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_12_MIXED_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_12_MIXED_CASE_WITH_NUMBER.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_12_MIXED_CASE_WITH_NUMBER_AND_SYMBOL.getIterations()) >= 0);
		
		// 16 characters, all lower case
		password = "aaaaaaaaaaaaaaaa";
		assertTrue(password.length() == 16);
		result = PasswordStrengthMeter.check(password, false);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_16_LOWER_CASE.getIterations()) >= 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_16_MIXED_CASE.getIterations()) < 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_16_MIXED_CASE_WITH_NUMBER.getIterations()) < 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_16_MIXED_CASE_WITH_NUMBER_AND_SYMBOL.getIterations()) < 0);
		
		// 16 characters mixed case
		password = "AAAAAAAAAAAAAAAa";
		assertTrue(password.length() == 16);
		result = PasswordStrengthMeter.check(password, false);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_16_LOWER_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_16_MIXED_CASE.getIterations()) >= 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_16_MIXED_CASE_WITH_NUMBER.getIterations()) < 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_16_MIXED_CASE_WITH_NUMBER_AND_SYMBOL.getIterations()) < 0);
		
		// 16 characters mixed case plus a number
		password = "00000000000000Aa";
		assertTrue(password.length() == 16);
		result = PasswordStrengthMeter.check(password, false);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_16_LOWER_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_16_MIXED_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_16_MIXED_CASE_WITH_NUMBER.getIterations()) >= 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_16_MIXED_CASE_WITH_NUMBER_AND_SYMBOL.getIterations()) < 0);
		
		// 16 characters mixed case plus a number and a non-alphanumeric symbol
		password = "!!!!!!!!!!!!!1Aa";
		assertTrue(password.length() == 16);
		result = PasswordStrengthMeter.check(password, false);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_16_LOWER_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_16_MIXED_CASE.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_16_MIXED_CASE_WITH_NUMBER.getIterations()) > 0);
		assertTrue(result.compareTo(PasswordStrengthClass.LENGTH_16_MIXED_CASE_WITH_NUMBER_AND_SYMBOL.getIterations()) >= 0);
		
	}
}
