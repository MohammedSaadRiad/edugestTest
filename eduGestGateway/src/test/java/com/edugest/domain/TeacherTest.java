package com.edugest.domain;

import static com.edugest.domain.SchoolClassTestSamples.*;
import static com.edugest.domain.SubjectTestSamples.*;
import static com.edugest.domain.TeacherTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edugest.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TeacherTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Teacher.class);
        Teacher teacher1 = getTeacherSample1();
        Teacher teacher2 = new Teacher();
        assertThat(teacher1).isNotEqualTo(teacher2);

        teacher2.setId(teacher1.getId());
        assertThat(teacher1).isEqualTo(teacher2);

        teacher2 = getTeacherSample2();
        assertThat(teacher1).isNotEqualTo(teacher2);
    }

    @Test
    void subjectsTest() {
        Teacher teacher = getTeacherRandomSampleGenerator();
        Subject subjectBack = getSubjectRandomSampleGenerator();

        teacher.addSubjects(subjectBack);
        assertThat(teacher.getSubjects()).containsOnly(subjectBack);

        teacher.removeSubjects(subjectBack);
        assertThat(teacher.getSubjects()).doesNotContain(subjectBack);

        teacher.subjects(new HashSet<>(Set.of(subjectBack)));
        assertThat(teacher.getSubjects()).containsOnly(subjectBack);

        teacher.setSubjects(new HashSet<>());
        assertThat(teacher.getSubjects()).doesNotContain(subjectBack);
    }

    @Test
    void schoolClassesTest() {
        Teacher teacher = getTeacherRandomSampleGenerator();
        SchoolClass schoolClassBack = getSchoolClassRandomSampleGenerator();

        teacher.addSchoolClasses(schoolClassBack);
        assertThat(teacher.getSchoolClasses()).containsOnly(schoolClassBack);
        assertThat(schoolClassBack.getTeachers()).containsOnly(teacher);

        teacher.removeSchoolClasses(schoolClassBack);
        assertThat(teacher.getSchoolClasses()).doesNotContain(schoolClassBack);
        assertThat(schoolClassBack.getTeachers()).doesNotContain(teacher);

        teacher.schoolClasses(new HashSet<>(Set.of(schoolClassBack)));
        assertThat(teacher.getSchoolClasses()).containsOnly(schoolClassBack);
        assertThat(schoolClassBack.getTeachers()).containsOnly(teacher);

        teacher.setSchoolClasses(new HashSet<>());
        assertThat(teacher.getSchoolClasses()).doesNotContain(schoolClassBack);
        assertThat(schoolClassBack.getTeachers()).doesNotContain(teacher);
    }
}
