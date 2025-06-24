package com.edugest.domain;

import static com.edugest.domain.SchoolClassTestSamples.*;
import static com.edugest.domain.SubjectTestSamples.*;
import static com.edugest.domain.TeacherTestSamples.*;
import static com.edugest.domain.TimetableTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edugest.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SchoolClassTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SchoolClass.class);
        SchoolClass schoolClass1 = getSchoolClassSample1();
        SchoolClass schoolClass2 = new SchoolClass();
        assertThat(schoolClass1).isNotEqualTo(schoolClass2);

        schoolClass2.setId(schoolClass1.getId());
        assertThat(schoolClass1).isEqualTo(schoolClass2);

        schoolClass2 = getSchoolClassSample2();
        assertThat(schoolClass1).isNotEqualTo(schoolClass2);
    }

    @Test
    void timetableTest() {
        SchoolClass schoolClass = getSchoolClassRandomSampleGenerator();
        Timetable timetableBack = getTimetableRandomSampleGenerator();

        schoolClass.setTimetable(timetableBack);
        assertThat(schoolClass.getTimetable()).isEqualTo(timetableBack);

        schoolClass.timetable(null);
        assertThat(schoolClass.getTimetable()).isNull();
    }

    @Test
    void teachersTest() {
        SchoolClass schoolClass = getSchoolClassRandomSampleGenerator();
        Teacher teacherBack = getTeacherRandomSampleGenerator();

        schoolClass.addTeachers(teacherBack);
        assertThat(schoolClass.getTeachers()).containsOnly(teacherBack);

        schoolClass.removeTeachers(teacherBack);
        assertThat(schoolClass.getTeachers()).doesNotContain(teacherBack);

        schoolClass.teachers(new HashSet<>(Set.of(teacherBack)));
        assertThat(schoolClass.getTeachers()).containsOnly(teacherBack);

        schoolClass.setTeachers(new HashSet<>());
        assertThat(schoolClass.getTeachers()).doesNotContain(teacherBack);
    }

    @Test
    void subjectsTest() {
        SchoolClass schoolClass = getSchoolClassRandomSampleGenerator();
        Subject subjectBack = getSubjectRandomSampleGenerator();

        schoolClass.addSubjects(subjectBack);
        assertThat(schoolClass.getSubjects()).containsOnly(subjectBack);
        assertThat(subjectBack.getSchoolClasses()).containsOnly(schoolClass);

        schoolClass.removeSubjects(subjectBack);
        assertThat(schoolClass.getSubjects()).doesNotContain(subjectBack);
        assertThat(subjectBack.getSchoolClasses()).doesNotContain(schoolClass);

        schoolClass.subjects(new HashSet<>(Set.of(subjectBack)));
        assertThat(schoolClass.getSubjects()).containsOnly(subjectBack);
        assertThat(subjectBack.getSchoolClasses()).containsOnly(schoolClass);

        schoolClass.setSubjects(new HashSet<>());
        assertThat(schoolClass.getSubjects()).doesNotContain(subjectBack);
        assertThat(subjectBack.getSchoolClasses()).doesNotContain(schoolClass);
    }
}
