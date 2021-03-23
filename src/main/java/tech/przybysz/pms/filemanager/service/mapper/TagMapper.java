package tech.przybysz.pms.filemanager.service.mapper;

import org.mapstruct.Mapper;
import tech.przybysz.pms.filemanager.domain.Tag;
import tech.przybysz.pms.filemanager.service.dto.TagDTO;

/**
 * Mapper for the entity {@link Tag} and its DTO {@link TagDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TagMapper extends EntityMapper<TagDTO, Tag> {

  TagDTO toDto(Tag tag);

  Tag toEntity(TagDTO tagDTO);

  default Tag fromId(Long id) {
    if(id == null) {
      return null;
    }
    Tag tag = new Tag();
    tag.setId(id);
    return tag;
  }
}
