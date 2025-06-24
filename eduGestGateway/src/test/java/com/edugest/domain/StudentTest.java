package com.edugest.domain;

import static com.edugest.domain.ParentTestSamples.*;
import static com.edugest.domain.SchoolClassTestSamples.*;
import static com.edugest.domain.StudentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edugest.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StudentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Student.class);
        Student student1 = getStudentSample1();
        Student student2 = new Student();
        assertThat(student1).isNotEqualTo(student2);

        student2.setId(student1.getId());
        assertThat(student1).isEqualTo(student2);

        student2 = getStudentSample2();
        assertThat(student1).isNotEqualTo(student2);
    }

    @Test
    void schoolClassTest() {
        Student student = getStudentRandomSampleGenerator();
        SchoolClass schoolClassBack = getSchoolClassRandomSampleGenerator();

        student.setSchoolClass(schoolClassBack);
        assertThat(student.getSchoolClass()).isEqualTo(schoolClassBack);

        student.schoolClass(null);
        assertThat(student.getSchoolClass()).isNull();
    }

    @Test
    void parentsTest() {
        Student student = getStudentRandomSampleGenerator();
        Parent parentBack = getParentRandomSampleGenerator();

        student.addParents(parentBack);
        assertThat(student.getParents()).containsOnly(parentBack);

        student.removeParents(parentBack);
        assertThat(student.getParents()).doesNotContain(parentBack);

        student.parents(new HashSet<>(Set.of(parentBack)));
        assertThat(student.getParents()).containsOnly(parentBack);

        student.setParents(new HashSet<>());
        assertThat(student.getParents()).doesNotContain(parentBack);
    }
}
