package tech.przybysz.pms.filemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.przybysz.pms.filemanager.domain.Tag;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

  Collection<Tag> findAllByNameContainsIgnoreCase(String val);

  Optional<Tag> findByName(String val);

  boolean existsByName(String val);

  void deleteByIdIn(Collection<Long> ids);
}
