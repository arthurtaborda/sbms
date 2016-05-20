package net.artcoder;

import net.artcoder.domain.Backup;
import net.artcoder.domain.IP;
import net.artcoder.domain.exception.InvalidIPAddressException;
import net.artcoder.persistence.entity.BackupEntity;

import java.util.Date;

public class TestUtil {

	public static IP getIP(String digit) {
		IP defaultIP = null;
		try {
			defaultIP = new IP("1.1.1.1");
			return new IP(digit + "." + digit + "." + digit + "." + digit);
		} catch (InvalidIPAddressException e) {
			return defaultIP;
		}
	}

	public static Backup getBackup(Integer id) {
		return new Backup("id" + id, "s1" + id, "sd" + id, "d" + id, "dd" + id);
	}

	public static BackupEntity getBackupEntity(String id) {
		return getBackupEntity(id, null);
	}

	public static BackupEntity getBackupEntity(String id, Date date) {
		if(date == null)
			date = new Date();
		return new BackupEntity(id, "s" + id, "sd" + id, "d" + id, "dd" + id, date, getIP(id).toString());
	}
}
