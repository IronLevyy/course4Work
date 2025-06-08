package pro.sky.telegrambot.model;


import javax.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_task")
public class NotificationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "notification_text", nullable = false)
    private String notificationText;

    @Column(name = "notification_datetime", nullable = false)
    private LocalDateTime notificationDateTime;

    @Column(name = "sent", nullable = false)
    private boolean sent = false;


    public NotificationTask() {}

    public NotificationTask(Long chatId, String notificationText, LocalDateTime notificationDateTime) {
        this.chatId = chatId;
        this.notificationText = notificationText;
        this.notificationDateTime = notificationDateTime;
    }

    public Long getId() {
        return id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public LocalDateTime getNotificationDateTime() {
        return notificationDateTime;
    }

    public void setNotificationDateTime(LocalDateTime notificationDateTime) {
        this.notificationDateTime = notificationDateTime;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}

