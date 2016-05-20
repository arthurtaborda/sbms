package net.artcoder.infrastructure;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;
import net.artcoder.domain.BackupFile;

import java.io.IOException;

class SambaBackupFile implements BackupFile {

	private SmbFile file;

	SambaBackupFile(SmbFile file) {
		this.file = file;
	}

	SmbFile getFile() {
		return file;
	}

	@Override
	public boolean exists() throws IOException {
		try {
			return file.exists();
		} catch (SmbException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean canRead() throws IOException {
		try {
			return file.canRead();
		} catch (SmbException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean canWrite() throws IOException {
		try {
			return file.canWrite();
		} catch (SmbException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean isFile() throws IOException {
		try {
			return file.isFile();
		} catch (SmbException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean isDirectory() throws IOException {
		try {
			return file.isDirectory();
		} catch (SmbException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void copyTo(BackupFile dest) throws IOException {
		SmbFile smbFile;
		try {
			smbFile = ((SambaBackupFile) dest).getFile();
		} catch (Exception e) {
			throw new IOException("Context must be a SambaBackupFile");
		}

		try {
			file.copyTo(smbFile);
		} catch (SmbException e) {
			throw new IOException(e);
		}
	}

	@Override
	public String getName() throws IOException {
		try {
			return file.getName();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public BackupFile[] listFiles() throws IOException {
		return listFilter(SmbFile::isFile);
	}

	@Override
	public BackupFile[] listDirectories() throws IOException {
		return listFilter(SmbFile::isDirectory);
	}

	private BackupFile[] listFilter(SmbFileFilter filter) throws IOException {
		BackupFile[] backupFiles;
		try {
			SmbFile[] smbFiles = file.listFiles(filter);
			backupFiles = new BackupFile[smbFiles.length];

			for (int i = 0; i < smbFiles.length; i++) {
				backupFiles[i] = new SambaBackupFile(smbFiles[i]);
			}
		} catch (SmbException e) {
			throw new IOException(e);
		}

		return backupFiles;
	}

	@Override
	public void mkdir() throws IOException {
		try {
			file.mkdir();
		} catch (SmbException e) {
			throw new IOException(e);
		}
	}
}
