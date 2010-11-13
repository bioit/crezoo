package org.tgdb.util;

import java.util.GregorianCalendar;
import java.util.Calendar;

public class ArxCalendar extends GregorianCalendar {
    
    public ArxCalendar() {
    }
    
    public int getWorkingHoursForMonth(int hoursPerDay) {
        // Helper calendar
        Calendar aDay = null;
        
        // Time sum
        int totalHours = 0;
        
        // Get the number of days in that month
        int days = getActualMaximum(Calendar.DAY_OF_MONTH);
        
        for(int i=0;i<days;i++) {
            aDay = new GregorianCalendar(2006, get(Calendar.MONTH), i);
            int weekDay = aDay.get(Calendar.DAY_OF_WEEK);
            
            if((weekDay != Calendar.SATURDAY) &&  (weekDay != Calendar.SUNDAY)){
                totalHours += hoursPerDay;
            }
        }
        
        return totalHours;
    }
    
    public int daysInMonth() {
        return getActualMaximum(Calendar.DAY_OF_MONTH);
    }
    
    public void nextMonth() {
        roll(Calendar.MONTH, 1);
    }
    
    public void previousMonth() {
        roll(Calendar.MONTH, -1);
    }
    
    public int workingHoursForDay(int day, int hoursPerDay) {
        set(Calendar.DAY_OF_MONTH, day);
        int weekDay = get(Calendar.DAY_OF_WEEK);
        
        if((weekDay != Calendar.SATURDAY) &&  (weekDay != Calendar.SUNDAY)){
            return hoursPerDay;
        } else
            return 0;
    }
    
    /*
    public String nameOfMonth(boolean shortForm) {
        if(shortForm)
            return String.format("%1$tb", this);
        else
            return String.format("%1$tB", this);
    }
    
    public String nameOfWeekday(int dateInMonth, boolean shortForm) {
        set(Calendar.DAY_OF_WEEK, dateInMonth);
        if(shortForm)
            return String.format("%1$ta", this);
        else
            return String.format("%1$tA", this);
    }
     */
}