package com.edugest.domain;

import static com.edugest.domain.GradesTestSamples.*;
import static com.edugest.domain.StudentTestSamples.*;
import static com.edugest.domain.SubjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edugest.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GradesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Grades.class);
        Grades grades1 = getGradesSample1();
        Grades grades2 = new Grades();
        assertThat(grades1).isNotEqualTo(grades2);

        grades2.setId(grades1.getId());
        assertThat(grades1).isEqualTo(grades2);

        grades2 = getGradesSample2();
        assertThat(grades1).isNotEqualTo(grades2);
    }

    @Test
    void studentTest() {
        Grades grades = getGradesRandomSampleGenerator();
        Student studentBack = getStudentRandomSampleGenerator();

        grades.setStudent(studentBack);
        assertThat(grades.getStudent()).isEqualTo(studentBack);

        grades.student(null);
        assertThat(grades.getStudent()).isNull();
    }

    @Test
    void subjectTest() {
        Grades grades = getGradesRandomSampleGenerator();
        Subject subjectBack = getSubjectRandomSampleGenerator();

        grades.setSubject(subjectBack);
        assertThat(grades.getSubject()).isEqualTo(subjectBack);

        grades.subject(null);
        assertThat(grades.getSubject()).isNull();
    }
}
