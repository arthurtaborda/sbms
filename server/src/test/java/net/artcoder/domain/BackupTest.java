package net.artcoder.domain;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class BackupTest {

	@Test
	public void testSetCredentials() throws Exception {
		Backup backup = new Backup("id", "s1", "sd1", "d1", "dd1");

		List<Pair<String, String>> values = new ArrayList<>();
		values.add(new ImmutablePair<>("user", null));
		values.add(new ImmutablePair<>(null, "pass"));
		values.add(new ImmutablePair<>(null, null));

		try {
			for (Pair<String, String> pair : values) {
				backup.setSourceAccessCredentials(pair.getLeft(), pair.getRight());
			}
			assertTrue("Should have thrown exception", false);
		} catch (Exception ignored) {
		}

		try {
			for (Pair<String, String> pair : values) {
				backup.setDestinationAccessCredentials(pair.getLeft(), pair.getRight());
			}
			assertTrue("Should have thrown exception", false);
		} catch (Exception ignored) {
		}
	}
}