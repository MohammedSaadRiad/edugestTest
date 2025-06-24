package com.edugest.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StudentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Student getStudentSample1() {
        return new Student()
            .id(1L)
            .identifier("identifier1")
            .nationality("nationality1")
            .phoneNumber("phoneNumber1")
            .address("address1")
            .note("note1");
    }

    public static Student getStudentSample2() {
        return new Student()
            .id(2L)
            .identifier("identifier2")
            .nationality("nationality2")
            .phoneNumber("phoneNumber2")
            .address("address2")
            .note("note2");
    }

    public static Student getStudentRandomSampleGenerator() {
        return new Student()
            .id(longCount.incrementAndGet())
            .identifier(UUID.randomUUID().toString())
            .nationality(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .note(UUID.randomUUID().toString());
    }
}
