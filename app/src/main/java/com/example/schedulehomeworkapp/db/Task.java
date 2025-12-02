package com.example.schedulehomeworkapp.db;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Task {
    public long id;
    public String title;
    public long disciplineId;
    public String disciplineName;
    public Date deadline;
    public int priority;
    public boolean done;

    public boolean isOverdue() {
        if (deadline == null) {
            return false;
        }
        return deadline.getTime() < System.currentTimeMillis() && !done;
    }

    public String getDeadlineLabel() {
        if (deadline == null) {
            return "";
        }

        long now = System.currentTimeMillis();
        long diff = deadline.getTime() - now;
        long days = TimeUnit.MILLISECONDS.toDays(diff);

        if (days == 0) {
            return "Сегодня, " + formatDate();
        } else if (days == 1) {
            return "Завтра, " + formatDate();
        } else if (days == -1) {
            return "Вчера, " + formatDate();
        } else {
            if (days > 1) {
                return "Через " + days + " дн., " + formatDate();
            } else {
                return (Math.abs(days)) + " дн. назад, " + formatDate();
            }
        }
    }

    private String formatDate() {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return df.format(deadline);
    }
}