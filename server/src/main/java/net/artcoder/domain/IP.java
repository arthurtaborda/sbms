package net.artcoder.domain;

import lombok.EqualsAndHashCode;
import net.artcoder.domain.exception.InvalidIPAddressException;

import java.util.regex.Pattern;

@EqualsAndHashCode(of = {"address"})
public class IP {
	private static final Pattern PATTERN = Pattern.compile(
			"^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	private String address;

	public IP(String address) {
		if(!validate(address)) {
			throw new InvalidIPAddressException("Address is not valid");
		}
		this.address = address;
	}

	private boolean validate(final String ip) {
		return PATTERN.matcher(ip).matches();
	}

	@Override
	public String toString() {
		return address;
	}
}
