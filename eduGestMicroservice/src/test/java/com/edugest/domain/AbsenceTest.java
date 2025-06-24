package com.edugest.domain;

import static com.edugest.domain.AbsenceTestSamples.*;
import static com.edugest.domain.SessionTestSamples.*;
import static com.edugest.domain.StudentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edugest.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AbsenceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Absence.class);
        Absence absence1 = getAbsenceSample1();
        Absence absence2 = new Absence();
        assertThat(absence1).isNotEqualTo(absence2);

        absence2.setId(absence1.getId());
        assertThat(absence1).isEqualTo(absence2);

        absence2 = getAbsenceSample2();
        assertThat(absence1).isNotEqualTo(absence2);
    }

    @Test
    void studentTest() {
        Absence absence = getAbsenceRandomSampleGenerator();
        Student studentBack = getStudentRandomSampleGenerator();

        absence.setStudent(studentBack);
        assertThat(absence.getStudent()).isEqualTo(studentBack);

        absence.student(null);
        assertThat(absence.getStudent()).isNull();
    }

    @Test
    void sessionTest() {
        Absence absence = getAbsenceRandomSampleGenerator();
        Session sessionBack = getSessionRandomSampleGenerator();

        absence.setSession(sessionBack);
        assertThat(absence.getSession()).isEqualTo(sessionBack);

        absence.session(null);
        assertThat(absence.getSession()).isNull();
    }
}
