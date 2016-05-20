package net.artcoder.domain;

import net.artcoder.domain.exception.InvalidIPAddressException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IPTest {

	@Test
	public void testEquals() throws InvalidIPAddressException {
		IP ip1 = new IP("255.255.255.255");
		IP ip2 = new IP("255.255.255.255");

		assertEquals(ip1, ip2);
	}

	@Test
	public void testValidIPAddresses() {
		List<String> validAddresses = new ArrayList<>();
		validAddresses.add("1.1.1.1");
		validAddresses.add("12.12.12.12");
		validAddresses.add("123.123.123.123");
		validAddresses.add("1.12.123.123");
		validAddresses.add("1.123.123.123");
		validAddresses.add("255.255.255.255");
		validAddresses.add("8.245.6.9");
		validAddresses.add("8.245.6.9");

		for (String validAddress : validAddresses) {
			try {
				new IP(validAddress);
			} catch (Exception e) {
				assertTrue("IP is not valid: " + validAddress, false);
			}
		}
	}

	@Test
	public void testInvalidIPAddresses() {
		List<String> validAddresses = new ArrayList<>();
		validAddresses.add("256.256.256.256");
		validAddresses.add("1265.1.2.2");
		validAddresses.add("123.123.123.l12");
		validAddresses.add("hg.hgh.t.t");
		validAddresses.add("594891");
		validAddresses.add("8.245.600.9");

		for (String validAddress : validAddresses) {
			try {
				new IP(validAddress);
				assertTrue("IP is valid: " + validAddress, false);
			} catch (Exception ignored) {
			}
		}
	}

}