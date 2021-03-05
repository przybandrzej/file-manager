package tech.przybysz.pms.filemanager.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.przybysz.pms.filemanager.domain.Directory;
import tech.przybysz.pms.filemanager.service.dto.DirectoryDTO;

/**
 * Mapper for the entity {@link Directory} and its DTO {@link DirectoryDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface DirectoryMapper extends EntityMapper<DirectoryDTO, Directory> {

  @Mapping(source = "parent.id", target = "parentId")
  DirectoryDTO toDto(Directory directory);

  @Mapping(source = "parentId", target = "parent")
  @Mapping(target = "children", ignore = true)
  @Mapping(target = "files", ignore = true)
  Directory toEntity(DirectoryDTO directoryDTO);

  default Directory fromId(Long id) {
    if (id == null) {
      return null;
    }
    Directory directory = new Directory();
    directory.setId(id);
    return directory;
  }
}
