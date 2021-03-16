package tech.przybysz.pms.filemanager.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.przybysz.pms.filemanager.domain.ResourceFileLink;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileLinkDTO;

/**
 * Mapper for the entity {@link ResourceFileLink} and its DTO {@link ResourceFileLinkDTO}.
 */
@Mapper(componentModel = "spring", uses = {ResourceFileMapper.class})
public interface ResourceFileLinkMapper extends EntityMapper<ResourceFileLinkDTO, ResourceFileLink> {

  @Mapping(source = "parentFile.id", target = "parentFileId")
  @Mapping(source = "childFile.id", target = "childFileId")
  ResourceFileLinkDTO toDto(ResourceFileLink directory);

  @Mapping(source = "parentFileId", target = "parentFile")
  @Mapping(source = "childFileId", target = "childFile")
  ResourceFileLink toEntity(ResourceFileLinkDTO directoryDTO);

  default ResourceFileLink fromId(Long id) {
    if(id == null) {
      return null;
    }
    ResourceFileLink file = new ResourceFileLink();
    file.setId(id);
    return file;
  }
}
