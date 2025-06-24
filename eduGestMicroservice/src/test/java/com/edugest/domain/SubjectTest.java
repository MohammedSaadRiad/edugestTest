package com.edugest.domain;

import static com.edugest.domain.SchoolClassTestSamples.*;
import static com.edugest.domain.SubjectTestSamples.*;
import static com.edugest.domain.TeacherTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edugest.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SubjectTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Subject.class);
        Subject subject1 = getSubjectSample1();
        Subject subject2 = new Subject();
        assertThat(subject1).isNotEqualTo(subject2);

        subject2.setId(subject1.getId());
        assertThat(subject1).isEqualTo(subject2);

        subject2 = getSubjectSample2();
        assertThat(subject1).isNotEqualTo(subject2);
    }

    @Test
    void schoolClassesTest() {
        Subject subject = getSubjectRandomSampleGenerator();
        SchoolClass schoolClassBack = getSchoolClassRandomSampleGenerator();

        subject.addSchoolClasses(schoolClassBack);
        assertThat(subject.getSchoolClasses()).containsOnly(schoolClassBack);

        subject.removeSchoolClasses(schoolClassBack);
        assertThat(subject.getSchoolClasses()).doesNotContain(schoolClassBack);

        subject.schoolClasses(new HashSet<>(Set.of(schoolClassBack)));
        assertThat(subject.getSchoolClasses()).containsOnly(schoolClassBack);

        subject.setSchoolClasses(new HashSet<>());
        assertThat(subject.getSchoolClasses()).doesNotContain(schoolClassBack);
    }

    @Test
    void teachersTest() {
        Subject subject = getSubjectRandomSampleGenerator();
        Teacher teacherBack = getTeacherRandomSampleGenerator();

        subject.addTeachers(teacherBack);
        assertThat(subject.getTeachers()).containsOnly(teacherBack);
        assertThat(teacherBack.getSubjects()).containsOnly(subject);

        subject.removeTeachers(teacherBack);
        assertThat(subject.getTeachers()).doesNotContain(teacherBack);
        assertThat(teacherBack.getSubjects()).doesNotContain(subject);

        subject.teachers(new HashSet<>(Set.of(teacherBack)));
        assertThat(subject.getTeachers()).containsOnly(teacherBack);
        assertThat(teacherBack.getSubjects()).containsOnly(subject);

        subject.setTeachers(new HashSet<>());
        assertThat(subject.getTeachers()).doesNotContain(teacherBack);
        assertThat(teacherBack.getSubjects()).doesNotContain(subject);
    }
}
