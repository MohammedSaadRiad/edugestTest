package com.edugest.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TimetableTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Timetable getTimetableSample1() {
        return new Timetable().id(1L).semestre(1);
    }

    public static Timetable getTimetableSample2() {
        return new Timetable().id(2L).semestre(2);
    }

    public static Timetable getTimetableRandomSampleGenerator() {
        return new Timetable().id(longCount.incrementAndGet()).semestre(intCount.incrementAndGet());
    }
}
