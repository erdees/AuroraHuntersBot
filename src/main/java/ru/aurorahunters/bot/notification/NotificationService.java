package ru.aurorahunters.bot.notification;

import ru.aurorahunters.bot.Config;
import ru.aurorahunters.bot.dao.DSCOVRDataDAO;
import ru.aurorahunters.bot.model.solardata.SolarWindData;
import ru.aurorahunters.bot.service.solarwind.SourceIds;

import java.sql.SQLException;
import java.util.List;
import static java.lang.System.*;

public class NotificationService implements Runnable  {

    private static int minuteCounter;

    /**
     * Run method which is necessary to run it as a daemon using ScheduledExecutorService.
     */
    @Override
    public void run() {
        try {
            sendMessageOrWait();
        } catch (Exception e) {
            out.println("Caught exception in ScheduledExecutorService. StackTrace:\n" + e);
        }
    }

    private void sendMessageOrWait() throws InterruptedException, SQLException {
        List<SolarWindData> lastFiveMin =  new DSCOVRDataDAO(SourceIds.DSCOVR.getId()).getNotificationValues();
        try {
            if (new NotificationChecker().checkNotification(lastFiveMin)) {
                if (isSendTime()) {
                    MessageBuilder messageBuilder = new MessageBuilder();
                    String text = messageBuilder.getAlarmString(lastFiveMin);
                    new NotificationSender().sendNotif(text);
                    resetCounter();
                }
                incrementCounter();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void incrementCounter() {
        minuteCounter++;
    }

    private static void resetCounter() {
        minuteCounter = 0;
    }

    private boolean isSendTime() {
        return minuteCounter == 0 || minuteCounter == Config.getNotifyInterval();
    }
}