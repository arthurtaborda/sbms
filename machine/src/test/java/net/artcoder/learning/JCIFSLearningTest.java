package net.artcoder.learning;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

public class JCIFSLearningTest {

	@Test
	@Ignore
	public void testAccessProtectedFile() throws Exception {
		NtlmPasswordAuthentication sourceAuth = new NtlmPasswordAuthentication("gs;gustavo:gunners");
		String sourcePath = "smb://gs/users/Gustavo/BACKUP2/";
		SmbFile sambaSourceFile = new SmbFile(sourcePath, sourceAuth);

		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("gs;gustavo:gunners");
		String path = "smb://gs/users/Gustavo/BACKUP/";
		SmbFile sambaFile = new SmbFile(path, auth);
		copyFolder(sambaSourceFile, sambaFile);

	}

	public void copyFolder(SmbFile src, SmbFile dest) throws IOException {

		if (src.isDirectory()) {
			createDirectoryIfNotExists(src, dest);

			SmbFile files[] = src.listFiles(SmbFile::isFile);
			SmbFile directories[] = src.listFiles(SmbFile::isDirectory);

			copyFiles(dest, files);
			copyDirectories(dest, directories);
		} else {
			src.copyTo(dest);
		}
	}

	private void copyDirectories(SmbFile dest, SmbFile[] directories) throws IOException {
		for (SmbFile dir : directories) {
			String dirName = dir.getName();
			dirName = appendSlash(dirName);
			SmbFile destFile = new SmbFile(dest, dirName);
			copyFolder(dir, destFile);
		}
	}

	private void copyFiles(SmbFile dest, SmbFile[] files) throws IOException {
		for (SmbFile file : files) {
			SmbFile destFile = new SmbFile(dest, file.getName());
			copyFolder(file, destFile);
		}
	}

	private void createDirectoryIfNotExists(SmbFile src, SmbFile dest) throws SmbException {
		if (!dest.exists()) {
			dest.mkdir();
			System.out.println("Directory copied from " + src + "  to " + dest);
		}
	}

	private String appendSlash(String dirName) {
		if(dirName.lastIndexOf('/') != (dirName.length() - 1))
			dirName += "/";
		return dirName;
	}
}
