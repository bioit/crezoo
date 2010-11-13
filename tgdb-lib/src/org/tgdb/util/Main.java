package org.tgdb.util;

public class Main {
    
    public static void main(String[] args) {
        // Create calendar
        ArxCalendar cal = new ArxCalendar();
        // Set start point (year, month of year (0=january), day)
        cal.set(2006, 0, 1);
        
        // Days in month
        System.out.println("Days in month: "+cal.daysInMonth());
        
        // Name of month
        boolean shortForm = true;
        //System.out.println("Name of month: "+cal.nameOfMonth(shortForm));
        shortForm = false;
        //System.out.println("Name of month: "+cal.nameOfMonth(shortForm));
        
        // Name of weekday
        shortForm = true;
        //System.out.println("Name of weekday: "+cal.nameOfWeekday(12, shortForm));
        shortForm = false;
        //System.out.println("Name of weekday: "+cal.nameOfWeekday(12, shortForm));
        
        // Get hours of work for month
        int hours = cal.getWorkingHoursForMonth(8);
        System.out.println("Hours: "+hours);
        
        // Go to next month
        cal.nextMonth();
        hours = cal.getWorkingHoursForMonth(8);
        System.out.println("Hours: "+hours);
        
        // Go to next month
        cal.nextMonth();
        hours = cal.getWorkingHoursForMonth(8);
        System.out.println("Hours: "+hours);
        
        // Go to previous month
        cal.previousMonth();
        hours = cal.getWorkingHoursForMonth(8);
        System.out.println("Hours: "+hours);
        
        // Hours for a particular date in month
        hours = cal.workingHoursForDay(3, 8);
        System.out.println("Hours: "+hours);
    }
}