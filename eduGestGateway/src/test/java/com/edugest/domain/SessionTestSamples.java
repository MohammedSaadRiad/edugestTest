package com.edugest.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SessionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Session getSessionSample1() {
        return new Session().id(1L).day("day1").startTime("startTime1").endTime("endTime1").semester(1);
    }

    public static Session getSessionSample2() {
        return new Session().id(2L).day("day2").startTime("startTime2").endTime("endTime2").semester(2);
    }

    public static Session getSessionRandomSampleGenerator() {
        return new Session()
            .id(longCount.incrementAndGet())
            .day(UUID.randomUUID().toString())
            .startTime(UUID.randomUUID().toString())
            .endTime(UUID.randomUUID().toString())
            .semester(intCount.incrementAndGet());
    }
}
