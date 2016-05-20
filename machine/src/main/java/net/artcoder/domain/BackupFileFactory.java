package net.artcoder.domain;

import java.io.IOException;

public interface BackupFileFactory {

	BackupFile create(BackupFile context, String fileName) throws IOException;

	BackupFile create(String url, String domain, String username, String password) throws IOException;
}
