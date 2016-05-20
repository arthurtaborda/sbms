package net.artcoder.infrastructure;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import net.artcoder.domain.BackupFile;
import net.artcoder.domain.BackupFileFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;

@Component
public class SambaBackupFileFactory implements BackupFileFactory {

	@Override
	public BackupFile create(BackupFile context, String fileName) throws IOException {
		SmbFile smbFile;
		try {
			smbFile = ((SambaBackupFile) context).getFile();
		} catch (Exception e) {
			throw new IOException("Context must be a SambaBackupFile");
		}

		return new SambaBackupFile(new SmbFile(smbFile, fileName));
	}

	@Override
	public BackupFile create(String url, String domain, String username, String password) throws IOException {
		NtlmPasswordAuthentication sourceAuth =
				new NtlmPasswordAuthentication(domain, username, password);
		try {
			return new SambaBackupFile(new SmbFile(url, sourceAuth));
		} catch (MalformedURLException e) {
			throw new IOException(e);
		}
	}
}
