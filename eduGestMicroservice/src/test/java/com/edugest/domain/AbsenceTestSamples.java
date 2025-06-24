package com.edugest.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AbsenceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Absence getAbsenceSample1() {
        return new Absence().id(1L).justification("justification1").note("note1");
    }

    public static Absence getAbsenceSample2() {
        return new Absence().id(2L).justification("justification2").note("note2");
    }

    public static Absence getAbsenceRandomSampleGenerator() {
        return new Absence().id(longCount.incrementAndGet()).justification(UUID.randomUUID().toString()).note(UUID.randomUUID().toString());
    }
}
