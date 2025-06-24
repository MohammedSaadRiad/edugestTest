package com.edugest.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TeacherTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Teacher getTeacherSample1() {
        return new Teacher()
            .id(1L)
            .identifier("identifier1")
            .qualification("qualification1")
            .experience(1)
            .phoneNumber("phoneNumber1")
            .address("address1")
            .type("type1")
            .note("note1");
    }

    public static Teacher getTeacherSample2() {
        return new Teacher()
            .id(2L)
            .identifier("identifier2")
            .qualification("qualification2")
            .experience(2)
            .phoneNumber("phoneNumber2")
            .address("address2")
            .type("type2")
            .note("note2");
    }

    public static Teacher getTeacherRandomSampleGenerator() {
        return new Teacher()
            .id(longCount.incrementAndGet())
            .identifier(UUID.randomUUID().toString())
            .qualification(UUID.randomUUID().toString())
            .experience(intCount.incrementAndGet())
            .phoneNumber(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .type(UUID.randomUUID().toString())
            .note(UUID.randomUUID().toString());
    }
}
