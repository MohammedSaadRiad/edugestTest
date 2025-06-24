package com.edugest.domain;

import static com.edugest.domain.ParentTestSamples.*;
import static com.edugest.domain.StudentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edugest.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ParentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Parent.class);
        Parent parent1 = getParentSample1();
        Parent parent2 = new Parent();
        assertThat(parent1).isNotEqualTo(parent2);

        parent2.setId(parent1.getId());
        assertThat(parent1).isEqualTo(parent2);

        parent2 = getParentSample2();
        assertThat(parent1).isNotEqualTo(parent2);
    }

    @Test
    void studentsTest() {
        Parent parent = getParentRandomSampleGenerator();
        Student studentBack = getStudentRandomSampleGenerator();

        parent.addStudents(studentBack);
        assertThat(parent.getStudents()).containsOnly(studentBack);
        assertThat(studentBack.getParents()).containsOnly(parent);

        parent.removeStudents(studentBack);
        assertThat(parent.getStudents()).doesNotContain(studentBack);
        assertThat(studentBack.getParents()).doesNotContain(parent);

        parent.students(new HashSet<>(Set.of(studentBack)));
        assertThat(parent.getStudents()).containsOnly(studentBack);
        assertThat(studentBack.getParents()).containsOnly(parent);

        parent.setStudents(new HashSet<>());
        assertThat(parent.getStudents()).doesNotContain(studentBack);
        assertThat(studentBack.getParents()).doesNotContain(parent);
    }
}
