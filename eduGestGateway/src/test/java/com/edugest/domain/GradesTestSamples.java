package com.edugest.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class GradesTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Grades getGradesSample1() {
        return new Grades().id(1L);
    }

    public static Grades getGradesSample2() {
        return new Grades().id(2L);
    }

    public static Grades getGradesRandomSampleGenerator() {
        return new Grades().id(longCount.incrementAndGet());
    }
}
