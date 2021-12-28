package si.fri.rsoteam.dtos;

import java.time.Instant;

public class NotificationLogDto {
    public Integer id;
    public Instant timestamp;
    public Instant sentAt;
    public String receiver;
    public String sender;
    public String content;

    public NotificationLogDto() {
    }

    public NotificationLogDto NotificationLogDto() {
        return new NotificationLogDto();
    }

    public NotificationLogDto setId(Integer id) {
        this.id = id;
        return this;
    }

    public NotificationLogDto setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public NotificationLogDto setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
        return this;
    }

    public NotificationLogDto setReceiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    public NotificationLogDto setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public NotificationLogDto setContent(String content) {
        this.content = content;
        return this;
    }
}
