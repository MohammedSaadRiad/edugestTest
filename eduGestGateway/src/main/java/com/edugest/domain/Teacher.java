package com.edugest.domain;

import com.edugest.domain.enumeration.Genders;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Teacher.
 */
@Table("teacher")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Teacher implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("identifier")
    private String identifier;

    @Column("birth_date")
    private LocalDate birthDate;

    @Column("qualification")
    private String qualification;

    @Column("gender")
    private Genders gender;

    @Column("experience")
    private Integer experience;

    @Column("phone_number")
    private String phoneNumber;

    @Column("address")
    private String address;

    @Column("type")
    private String type;

    @Column("note")
    private String note;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "schoolClasses", "teachers" }, allowSetters = true)
    private Set<Subject> subjects = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "timetable", "teachers", "subjects" }, allowSetters = true)
    private Set<SchoolClass> schoolClasses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Teacher id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public Teacher identifier(String identifier) {
        this.setIdentifier(identifier);
        return this;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public Teacher birthDate(LocalDate birthDate) {
        this.setBirthDate(birthDate);
        return this;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getQualification() {
        return this.qualification;
    }

    public Teacher qualification(String qualification) {
        this.setQualification(qualification);
        return this;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public Genders getGender() {
        return this.gender;
    }

    public Teacher gender(Genders gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(Genders gender) {
        this.gender = gender;
    }

    public Integer getExperience() {
        return this.experience;
    }

    public Teacher experience(Integer experience) {
        this.setExperience(experience);
        return this;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Teacher phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return this.address;
    }

    public Teacher address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return this.type;
    }

    public Teacher type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return this.note;
    }

    public Teacher note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Set<Subject> getSubjects() {
        return this.subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public Teacher subjects(Set<Subject> subjects) {
        this.setSubjects(subjects);
        return this;
    }

    public Teacher addSubjects(Subject subject) {
        this.subjects.add(subject);
        return this;
    }

    public Teacher removeSubjects(Subject subject) {
        this.subjects.remove(subject);
        return this;
    }

    public Set<SchoolClass> getSchoolClasses() {
        return this.schoolClasses;
    }

    public void setSchoolClasses(Set<SchoolClass> schoolClasses) {
        if (this.schoolClasses != null) {
            this.schoolClasses.forEach(i -> i.removeTeachers(this));
        }
        if (schoolClasses != null) {
            schoolClasses.forEach(i -> i.addTeachers(this));
        }
        this.schoolClasses = schoolClasses;
    }

    public Teacher schoolClasses(Set<SchoolClass> schoolClasses) {
        this.setSchoolClasses(schoolClasses);
        return this;
    }

    public Teacher addSchoolClasses(SchoolClass schoolClass) {
        this.schoolClasses.add(schoolClass);
        schoolClass.getTeachers().add(this);
        return this;
    }

    public Teacher removeSchoolClasses(SchoolClass schoolClass) {
        this.schoolClasses.remove(schoolClass);
        schoolClass.getTeachers().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Teacher)) {
            return false;
        }
        return getId() != null && getId().equals(((Teacher) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Teacher{" +
            "id=" + getId() +
            ", identifier='" + getIdentifier() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            ", qualification='" + getQualification() + "'" +
            ", gender='" + getGender() + "'" +
            ", experience=" + getExperience() +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", address='" + getAddress() + "'" +
            ", type='" + getType() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
