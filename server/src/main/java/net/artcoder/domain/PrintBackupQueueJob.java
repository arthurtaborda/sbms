package net.artcoder.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

@Slf4j
@Component
public class PrintBackupQueueJob {

	private ConcurrentBackupQueue backupQueue;

	@Autowired
	public PrintBackupQueueJob(ConcurrentBackupQueue backupQueue) {
		this.backupQueue = backupQueue;
	}

	@Scheduled(fixedRate = 1000)
	public void getAndRunBackups() {
		StringBuilder sb = new StringBuilder();
		Iterator<Map.Entry<IP, Queue<Backup>>> iter = backupQueue.getMap().entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<IP, Queue<Backup>> entry = iter.next();
			sb.append(entry.getKey());
			sb.append('=').append('"');
			sb.append(entry.getValue().size());
			sb.append('"');
			if (iter.hasNext()) {
				sb.append(',').append(' ');
			}
		}
		log.info(sb.toString() + " : SLOTS FREE: " + backupQueue.slotsFreeSize());
	}
}
