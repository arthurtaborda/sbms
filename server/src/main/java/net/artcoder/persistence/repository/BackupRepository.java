package net.artcoder.persistence.repository;

import net.artcoder.persistence.entity.BackupEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Repository
public interface BackupRepository extends CrudRepository<BackupEntity, String> {

	@Modifying
	@Query("update BackupEntity backup set backup.state = 'DONE' where backup.id = :backupId")
	void done(@Param("backupId") String backupId);

	@Modifying
	@Query("update BackupEntity backup set backup.state = 'SCHEDULED' where backup.id = :backupId")
	void schedule(@Param("backupId") String backupId);

	@Modifying
	@Transactional
	@Query("update BackupEntity backup set backup.state = 'QUEUED' where backup.id = :backupId")
	void queue(@Param("backupId") String backupId);

	@Modifying
	@Query("update BackupEntity backup set backup.state = 'DISABLED' where backup.id = :backupId ")
	void disable(@Param("backupId") String backupId);

	@Query("select case when (count(backup) > 0) then true else false end " +
			"from BackupEntity backup where backup.id = :backupId and backup.state = 'SCHEDULED'")
	boolean isEnabled(@Param("backupId") String backupId);

	@Query("select backup from BackupEntity backup where backup.ip = :ip")
	Set<BackupEntity> findByIp(@Param("ip") String ip);

	@Query("select backup from BackupEntity backup where backup.state = 'SCHEDULED' or backup.state = 'QUEUED'")
	Set<BackupEntity> findEnabledBackups();
}
