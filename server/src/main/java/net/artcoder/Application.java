package net.artcoder;

import net.artcoder.domain.BackupQueue;
import net.artcoder.domain.ConcurrentBackupQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ComponentScan("net.artcoder")
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement(proxyTargetClass = true)
public class Application {

	@Value("${timeout}")
	private Integer timeout;

	private static final Integer SLOT_SIZE = 1;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	BackupQueue backupQueue() {
		return new ConcurrentBackupQueue(SLOT_SIZE);
	}

	@Bean
	TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler();
	}

	@Bean
	Integer timeout() {
		return timeout;
	}
}