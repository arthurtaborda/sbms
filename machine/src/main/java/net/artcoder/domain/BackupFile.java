package net.artcoder.domain;

import java.io.IOException;

public interface BackupFile {
	boolean exists() throws IOException;

	boolean canRead() throws IOException;

	boolean canWrite() throws IOException;

	boolean isFile() throws IOException;

	boolean isDirectory() throws IOException;

	void copyTo(BackupFile dest) throws IOException;

	String getName() throws IOException;

	BackupFile[] listFiles() throws IOException;

	BackupFile[] listDirectories() throws IOException;

	void mkdir() throws IOException;
}
