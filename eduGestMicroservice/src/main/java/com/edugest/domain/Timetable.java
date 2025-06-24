package com.edugest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Timetable.
 */
@Table("timetable")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Timetable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("semestre")
    private Integer semestre;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "room", "subject", "teacher", "schoolClass", "timetable" }, allowSetters = true)
    private Set<Session> sessions = new HashSet<>();

    @org.springframework.data.annotation.Transient
    private SchoolClass schoolClass;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Timetable id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSemestre() {
        return this.semestre;
    }

    public Timetable semestre(Integer semestre) {
        this.setSemestre(semestre);
        return this;
    }

    public void setSemestre(Integer semestre) {
        this.semestre = semestre;
    }

    public Set<Session> getSessions() {
        return this.sessions;
    }

    public void setSessions(Set<Session> sessions) {
        if (this.sessions != null) {
            this.sessions.forEach(i -> i.setTimetable(null));
        }
        if (sessions != null) {
            sessions.forEach(i -> i.setTimetable(this));
        }
        this.sessions = sessions;
    }

    public Timetable sessions(Set<Session> sessions) {
        this.setSessions(sessions);
        return this;
    }

    public Timetable addSessions(Session session) {
        this.sessions.add(session);
        session.setTimetable(this);
        return this;
    }

    public Timetable removeSessions(Session session) {
        this.sessions.remove(session);
        session.setTimetable(null);
        return this;
    }

    public SchoolClass getSchoolClass() {
        return this.schoolClass;
    }

    public void setSchoolClass(SchoolClass schoolClass) {
        if (this.schoolClass != null) {
            this.schoolClass.setTimetable(null);
        }
        if (schoolClass != null) {
            schoolClass.setTimetable(this);
        }
        this.schoolClass = schoolClass;
    }

    public Timetable schoolClass(SchoolClass schoolClass) {
        this.setSchoolClass(schoolClass);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Timetable)) {
            return false;
        }
        return getId() != null && getId().equals(((Timetable) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Timetable{" +
            "id=" + getId() +
            ", semestre=" + getSemestre() +
            "}";
    }
}
