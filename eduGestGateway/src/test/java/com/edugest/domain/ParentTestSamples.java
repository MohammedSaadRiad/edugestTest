package com.edugest.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ParentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Parent getParentSample1() {
        return new Parent().id(1L).identifier("identifier1").phoneNumber("phoneNumber1").address("address1").note("note1");
    }

    public static Parent getParentSample2() {
        return new Parent().id(2L).identifier("identifier2").phoneNumber("phoneNumber2").address("address2").note("note2");
    }

    public static Parent getParentRandomSampleGenerator() {
        return new Parent()
            .id(longCount.incrementAndGet())
            .identifier(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .note(UUID.randomUUID().toString());
    }
}
