package tech.przybysz.pms.filemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.przybysz.pms.filemanager.domain.Directory;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface DirectoryRepository extends JpaRepository<Directory, Long> {

  Collection<Directory> findAllByParentId(Long parentId);
  Collection<Directory> findByNameAndParentId(String name, Long parentId);
}
