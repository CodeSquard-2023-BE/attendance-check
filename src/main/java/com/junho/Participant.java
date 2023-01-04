package com.junho;

import com.junho.constant.DayOfWeek;

import java.util.EnumMap;
import java.util.Objects;

import static com.junho.constant.DayOfWeek.DAY_OF_WEEK_COUNT;

public class Participant {
    private final String username;

    // TODO: 요일 / 참석여부
    private final EnumMap<DayOfWeek, Boolean> attendance;

    public Participant(String username) {
        this.username = username;
        this.attendance = new EnumMap<>(DayOfWeek.class);
    }

    public double getRate(){
        long count = this.attendance.values().stream()
                .filter(value -> value)
                .count();
        return (double) (count * 100) / DAY_OF_WEEK_COUNT;
    }

    public void checkAttendance(DayOfWeek day) {
        this.attendance.put(day, true);
    }

    public String getUsername() {
        return username;
    }

    public EnumMap<DayOfWeek, Boolean> getAttendance() {
        return attendance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return getUsername().equals(that.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername());
    }
}