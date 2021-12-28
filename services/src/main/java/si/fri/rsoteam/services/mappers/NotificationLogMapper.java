package si.fri.rsoteam.services.mappers;

import si.fri.rsoteam.dtos.NotificationLogDto;
import si.fri.rsoteam.models.entities.NotificationLogEntity;

public class NotificationLogMapper {
    public static NotificationLogDto entityToDto(NotificationLogEntity ue) {
        NotificationLogDto dto = new NotificationLogDto();
        dto.id = ue.getId();
        dto.sender = ue.getSender();
        dto.receiver = ue.getReceiver();
        dto.timestamp = ue.getTimestamp();
        dto.content = ue.getContent();
        dto.sentAt = ue.getSentAt();

        return dto;
    }

    public static NotificationLogEntity dtoToEntity(NotificationLogDto userDto) {
        NotificationLogEntity entity = new NotificationLogEntity();
        entity.setId(userDto.id);
        entity.setSender(userDto.sender);
        entity.setReceiver(userDto.receiver);
        entity.setTimestamp(userDto.timestamp);
        entity.setContent(userDto.content);
        entity.setSentAt(userDto.sentAt);

        return entity;
    }
}
