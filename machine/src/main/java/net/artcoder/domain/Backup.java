package net.artcoder.domain;

import lombok.*;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Backup {

	@NonNull
	private BackupFileFactory backupFileFactory;
	private final Queue<BackupExecution> executions = new ConcurrentLinkedQueue<>();

	private String id;
	private String source;
	private String sourceUser;
	private String sourcePass;
	private String sourceDomain;
	private String destination;
	private String destinationUser;
	private String destinationPass;
	private String destinationDomain;

	@Async
	public void start() {
		try {
			executions.add(BackupExecution.get(BackupExecution.Status.CONNECTING_TO_SOURCE));
			BackupFile sambaSourceFile = getSourceFile();

			executions.add(BackupExecution.get(BackupExecution.Status.CONNECTING_TO_DESTINATION));
			BackupFile sambaDestinationFile = getDestinationFile(sambaSourceFile);

			executions.add(BackupExecution.get(BackupExecution.Status.SENDING_TO_DESTINATION));
			copy(sambaSourceFile, sambaDestinationFile);

			executions.add(BackupExecution.get(BackupExecution.Status.DONE));
		} catch (IOException e) {
			executions.add(BackupExecution.get(BackupExecution.Status.ERROR, e.getMessage()));
		}
	}

	public Queue<BackupExecution> getExecutions() {
		return executions;
	}

	private BackupFile getSourceFile() throws IOException {
		BackupFile sambaSourceFile = backupFileFactory.create(source, sourceDomain, sourceUser, sourcePass);
		if (!sambaSourceFile.exists()) {
			throw new IOException("Source does not exist");
		}
		if (!sambaSourceFile.canRead()) {
			throw new IOException("Not able to read from source");
		}
		return sambaSourceFile;
	}

	private BackupFile getDestinationFile(BackupFile sambaSourceFile) throws IOException {
		BackupFile sambaDestinationFile = backupFileFactory.create(destination,
				destinationDomain, destinationUser, destinationPass);

		if (!sambaDestinationFile.exists()) {
			throw new IOException("Destination does not exist");
		}
		if (!sambaDestinationFile.canWrite()) {
			throw new IOException("Not able to write to destination");
		}
		if (sambaDestinationFile.isFile() && sambaSourceFile.isDirectory()) {
			throw new IOException("Cannot copy a directory into a file");
		}

		return sambaDestinationFile;
	}

	private void copy(BackupFile src, BackupFile dest) throws IOException {
		boolean isFile = !src.isDirectory();
		if (isFile) {
			src.copyTo(dest);
		} else {
			createDirectoryIfNotExists(src, dest);

			BackupFile files[] = src.listFiles();
			BackupFile directories[] = src.listDirectories();

			copyFiles(dest, files);
			copyDirectories(dest, directories);
		}
	}

	private void copyDirectories(BackupFile dest, BackupFile[] directories) throws IOException {
		for (BackupFile dir : directories) {
			String dirName = dir.getName();
			dirName = appendSlash(dirName);
			BackupFile destFile = backupFileFactory.create(dest, dirName);
			copy(dir, destFile);
		}
	}

	private void copyFiles(BackupFile dest, BackupFile[] files) throws IOException {
		for (BackupFile file : files) {
			BackupFile destFile = backupFileFactory.create(dest, file.getName());
			copy(file, destFile);
		}
	}

	private void createDirectoryIfNotExists(BackupFile src, BackupFile dest) throws IOException {
		if (!dest.exists()) {
			dest.mkdir();
			System.out.println("Directory copied from " + src + "  to " + dest);
		}
	}

	private String appendSlash(String dirName) {
		boolean hasSlash = dirName.lastIndexOf('/') == (dirName.length() - 1);
		if (!hasSlash)
			dirName += "/";
		return dirName;
	}
}
