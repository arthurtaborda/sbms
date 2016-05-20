package net.artcoder.persistence.repository;

import net.artcoder.persistence.entity.BackupExecutionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackupExecutionRepository extends CrudRepository<BackupExecutionEntity, String> {
}
