package com.junho;

import com.junho.constant.DayOfWeek;

import java.util.EnumMap;

public class Participant {

    public static final int WEEK_SIZE = 7;
    private final String username;

    // TODO: 요일 / 참석여부
    private final EnumMap<DayOfWeek, Boolean> attendance;

    public Participant(String username) {
        this.username = username;
        this.attendance = new EnumMap<DayOfWeek, Boolean>(DayOfWeek.class);
    }

    public double getRate(){
        long count = this.attendance.values().stream()
                .filter(value -> value == true)
                .count();
        return (count * 100) / WEEK_SIZE;
    }

    public void checkAttendance(DayOfWeek day) {
        this.attendance.put(day, true);
    }

    public String getUsername() {
        return username;
    }
}
