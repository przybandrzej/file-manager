package tech.przybysz.pms.filemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.przybysz.pms.filemanager.domain.Directory;

import java.util.Collection;

@Repository
public interface DirectoryRepository extends JpaRepository<Directory, Long> {

  Collection<Directory> findAllByParent(Long parentId);
}
