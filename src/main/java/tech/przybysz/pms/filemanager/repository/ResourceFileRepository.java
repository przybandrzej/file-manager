package tech.przybysz.pms.filemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.przybysz.pms.filemanager.domain.ResourceFile;

import java.util.Collection;

@Repository
public interface ResourceFileRepository extends JpaRepository<ResourceFile, Long> {

  Collection<ResourceFile> findAllByParentDirectoryId(Long parentDirectoryId);

  Collection<ResourceFile> findAllByExtension(String extension);

  Collection<ResourceFile> findAllByOriginalNameContainsIgnoreCase(String name);

  Collection<ResourceFile> findAllByTagsId(Long tagId);
}
