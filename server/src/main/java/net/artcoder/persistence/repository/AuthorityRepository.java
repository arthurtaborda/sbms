package net.artcoder.persistence.repository;

import net.artcoder.persistence.entity.AuthorityEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorityRepository extends CrudRepository<AuthorityEntity, Long> {

	@Query("select user.username from AuthorityEntity where authority = :role")
	List<String> findUsernamesFrom(@Param("role") String role);
}
