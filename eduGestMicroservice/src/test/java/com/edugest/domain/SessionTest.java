package com.edugest.domain;

import static com.edugest.domain.RoomTestSamples.*;
import static com.edugest.domain.SchoolClassTestSamples.*;
import static com.edugest.domain.SessionTestSamples.*;
import static com.edugest.domain.SubjectTestSamples.*;
import static com.edugest.domain.TeacherTestSamples.*;
import static com.edugest.domain.TimetableTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edugest.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Session.class);
        Session session1 = getSessionSample1();
        Session session2 = new Session();
        assertThat(session1).isNotEqualTo(session2);

        session2.setId(session1.getId());
        assertThat(session1).isEqualTo(session2);

        session2 = getSessionSample2();
        assertThat(session1).isNotEqualTo(session2);
    }

    @Test
    void roomTest() {
        Session session = getSessionRandomSampleGenerator();
        Room roomBack = getRoomRandomSampleGenerator();

        session.setRoom(roomBack);
        assertThat(session.getRoom()).isEqualTo(roomBack);

        session.room(null);
        assertThat(session.getRoom()).isNull();
    }

    @Test
    void subjectTest() {
        Session session = getSessionRandomSampleGenerator();
        Subject subjectBack = getSubjectRandomSampleGenerator();

        session.setSubject(subjectBack);
        assertThat(session.getSubject()).isEqualTo(subjectBack);

        session.subject(null);
        assertThat(session.getSubject()).isNull();
    }

    @Test
    void teacherTest() {
        Session session = getSessionRandomSampleGenerator();
        Teacher teacherBack = getTeacherRandomSampleGenerator();

        session.setTeacher(teacherBack);
        assertThat(session.getTeacher()).isEqualTo(teacherBack);

        session.teacher(null);
        assertThat(session.getTeacher()).isNull();
    }

    @Test
    void schoolClassTest() {
        Session session = getSessionRandomSampleGenerator();
        SchoolClass schoolClassBack = getSchoolClassRandomSampleGenerator();

        session.setSchoolClass(schoolClassBack);
        assertThat(session.getSchoolClass()).isEqualTo(schoolClassBack);

        session.schoolClass(null);
        assertThat(session.getSchoolClass()).isNull();
    }

    @Test
    void timetableTest() {
        Session session = getSessionRandomSampleGenerator();
        Timetable timetableBack = getTimetableRandomSampleGenerator();

        session.setTimetable(timetableBack);
        assertThat(session.getTimetable()).isEqualTo(timetableBack);

        session.timetable(null);
        assertThat(session.getTimetable()).isNull();
    }
}
