package com.edugest.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AdministrationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Administration getAdministrationSample1() {
        return new Administration()
            .id(1L)
            .identifier("identifier1")
            .phoneNumber("phoneNumber1")
            .address("address1")
            .type("type1")
            .note("note1");
    }

    public static Administration getAdministrationSample2() {
        return new Administration()
            .id(2L)
            .identifier("identifier2")
            .phoneNumber("phoneNumber2")
            .address("address2")
            .type("type2")
            .note("note2");
    }

    public static Administration getAdministrationRandomSampleGenerator() {
        return new Administration()
            .id(longCount.incrementAndGet())
            .identifier(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .type(UUID.randomUUID().toString())
            .note(UUID.randomUUID().toString());
    }
}
