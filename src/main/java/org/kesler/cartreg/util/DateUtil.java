package org.kesler.cartreg.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;


public class DateUtil {

    public static Date toBeginOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date!=null) {
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            date = calendar.getTime();
        }
        return date;
    }

    public static Date toEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date!=null) {
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY,23);
            calendar.set(Calendar.MINUTE,59);
            calendar.set(Calendar.SECOND,59);
            date = calendar.getTime();
        }
        return date;
    }

    public static Date localDateToDateBegDay(LocalDate localDate) {
        return localDate==null?null:Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date localDateToDateEndDay(LocalDate localDate) {
        if (localDate==null) return null;
        return toEndOfDay(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public static LocalDate dateToLocalDate(Date date) {
        return date==null?null:date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
