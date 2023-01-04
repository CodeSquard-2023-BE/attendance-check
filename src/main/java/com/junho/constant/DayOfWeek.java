package com.junho.constant;

import java.util.Arrays;

public enum DayOfWeek {
    MONDAY("월요일"),
    TUESDAY("화요일"),
    WEDNESDAY("수요일"),
    THURSDAY("목요일"),
    FRIDAY("금요일"),
    SATURDAY("토요일"),
    SUNDAY("일요일"),
    ;

    public static final int DAY_OF_WEEK_COUNT = 7;
    private final String dayOfTheWeek;

    DayOfWeek(String dayOfTheWeek) {
        this.dayOfTheWeek = dayOfTheWeek;
    }

    public static DayOfWeek findByDay(String day) {
        return Arrays.stream(DayOfWeek.values())
                .filter(dayOfWeek -> dayOfWeek.isEqualTo(day))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요일입니다."));
    }

    private boolean isEqualTo(String day) {
        return toString().equals(day);
    }

    @Override
    public String toString() {
        return dayOfTheWeek;
    }
}