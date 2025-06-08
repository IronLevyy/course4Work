package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationScheduler {

    @Autowired
    private NotificationTaskRepository repository;

    @Autowired
    private TelegramBot telegramBot;

    @Scheduled(cron = "0 * * * * *")
    public void sendDueNotifications() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        List<NotificationTask> dueTasks = repository.findAllDueNotifications(now);

        for (NotificationTask task : dueTasks) {
            telegramBot.execute(new SendMessage(task.getChatId(), task.getNotificationText()));

            task.setSent(true);
            repository.save(task);
        }
    }
}
