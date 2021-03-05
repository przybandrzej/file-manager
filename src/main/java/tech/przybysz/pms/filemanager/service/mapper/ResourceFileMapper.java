package tech.przybysz.pms.filemanager.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.przybysz.pms.filemanager.domain.Directory;
import tech.przybysz.pms.filemanager.domain.ResourceFile;
import tech.przybysz.pms.filemanager.service.dto.DirectoryDTO;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;

/**
 * Mapper for the entity {@link Directory} and its DTO {@link DirectoryDTO}.
 */
@Mapper(componentModel = "spring", uses = {DirectoryMapper.class})
public interface ResourceFileMapper extends EntityMapper<ResourceFileDTO, ResourceFile> {

  @Mapping(source = "parentDirectory.id", target = "parentDirectoryId")
  ResourceFileDTO toDto(ResourceFile directory);

  @Mapping(source = "parentDirectoryId", target = "parentDirectory")
  @Mapping(target = "children", ignore = true)
  @Mapping(target = "files", ignore = true)
  ResourceFile toEntity(ResourceFileDTO directoryDTO);

  default ResourceFile fromId(Long id) {
    if (id == null) {
      return null;
    }
    ResourceFile file = new ResourceFile();
    file.setId(id);
    return file;
  }
}
