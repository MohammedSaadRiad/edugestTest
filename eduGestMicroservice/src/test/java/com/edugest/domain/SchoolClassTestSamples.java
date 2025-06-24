package com.edugest.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SchoolClassTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SchoolClass getSchoolClassSample1() {
        return new SchoolClass().id(1L).name("name1").year(1);
    }

    public static SchoolClass getSchoolClassSample2() {
        return new SchoolClass().id(2L).name("name2").year(2);
    }

    public static SchoolClass getSchoolClassRandomSampleGenerator() {
        return new SchoolClass().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).year(intCount.incrementAndGet());
    }
}
