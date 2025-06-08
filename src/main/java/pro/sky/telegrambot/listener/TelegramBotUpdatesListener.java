package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationTaskRepository repository;

    private static final Pattern PATTERN = Pattern.compile("^(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}) (.+)$");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            if (update.message() != null && update.message().text() != null) {
                String text = update.message().text();
                Long chatId = update.message().chat().id();

                if ("/start".equals(text)) {
                    telegramBot.execute(new SendMessage(chatId, "Привет! Я бот-напоминалка.\n" +
                            "Пожалуйста, отправляй напоминания в формате:\n01.01.2022 20:00 Сделать домашнюю работу"));
                } else {
                    try {
                        NotificationTask task = parseNotification(text, chatId);
                        repository.save(task);
                        telegramBot.execute(new SendMessage(chatId, "Напоминание сохранено!"));
                    } catch (IllegalArgumentException e) {
                        telegramBot.execute(new SendMessage(chatId,
                                "Ошибка! Формат сообщения должен быть:\n01.01.2022 20:00 Сделать домашнюю работу"));
                    } catch (Exception e) {
                        logger.error("Ошибка при обработке сообщения", e);
                        telegramBot.execute(new SendMessage(chatId,
                                "Произошла ошибка при сохранении напоминания."));
                    }
                }
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private NotificationTask parseNotification(String text, Long chatId) {
        Matcher matcher = PATTERN.matcher(text);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Неверный формат");
        }
        String dateTimeStr = matcher.group(1);
        String messageText = matcher.group(2);

        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, FORMATTER);

        return new NotificationTask(chatId, messageText, dateTime);
    }
}
