package com.edugest.domain;

import static com.edugest.domain.AdministrationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edugest.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AdministrationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Administration.class);
        Administration administration1 = getAdministrationSample1();
        Administration administration2 = new Administration();
        assertThat(administration1).isNotEqualTo(administration2);

        administration2.setId(administration1.getId());
        assertThat(administration1).isEqualTo(administration2);

        administration2 = getAdministrationSample2();
        assertThat(administration1).isNotEqualTo(administration2);
    }
}
