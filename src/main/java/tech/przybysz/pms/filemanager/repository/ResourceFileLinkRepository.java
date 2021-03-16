package tech.przybysz.pms.filemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.przybysz.pms.filemanager.domain.ResourceFileLink;

import java.util.Collection;

@Repository
public interface ResourceFileLinkRepository extends JpaRepository<ResourceFileLink, Long> {

  Collection<ResourceFileLink> findAllByParentFileId(Long parentId);

  Collection<ResourceFileLink> findAllByChildFileId(Long childId);
}
