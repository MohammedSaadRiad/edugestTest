package com.edugest.domain;

import static com.edugest.domain.SchoolClassTestSamples.*;
import static com.edugest.domain.SessionTestSamples.*;
import static com.edugest.domain.TimetableTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edugest.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TimetableTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Timetable.class);
        Timetable timetable1 = getTimetableSample1();
        Timetable timetable2 = new Timetable();
        assertThat(timetable1).isNotEqualTo(timetable2);

        timetable2.setId(timetable1.getId());
        assertThat(timetable1).isEqualTo(timetable2);

        timetable2 = getTimetableSample2();
        assertThat(timetable1).isNotEqualTo(timetable2);
    }

    @Test
    void sessionsTest() {
        Timetable timetable = getTimetableRandomSampleGenerator();
        Session sessionBack = getSessionRandomSampleGenerator();

        timetable.addSessions(sessionBack);
        assertThat(timetable.getSessions()).containsOnly(sessionBack);
        assertThat(sessionBack.getTimetable()).isEqualTo(timetable);

        timetable.removeSessions(sessionBack);
        assertThat(timetable.getSessions()).doesNotContain(sessionBack);
        assertThat(sessionBack.getTimetable()).isNull();

        timetable.sessions(new HashSet<>(Set.of(sessionBack)));
        assertThat(timetable.getSessions()).containsOnly(sessionBack);
        assertThat(sessionBack.getTimetable()).isEqualTo(timetable);

        timetable.setSessions(new HashSet<>());
        assertThat(timetable.getSessions()).doesNotContain(sessionBack);
        assertThat(sessionBack.getTimetable()).isNull();
    }

    @Test
    void schoolClassTest() {
        Timetable timetable = getTimetableRandomSampleGenerator();
        SchoolClass schoolClassBack = getSchoolClassRandomSampleGenerator();

        timetable.setSchoolClass(schoolClassBack);
        assertThat(timetable.getSchoolClass()).isEqualTo(schoolClassBack);
        assertThat(schoolClassBack.getTimetable()).isEqualTo(timetable);

        timetable.schoolClass(null);
        assertThat(timetable.getSchoolClass()).isNull();
        assertThat(schoolClassBack.getTimetable()).isNull();
    }
}
