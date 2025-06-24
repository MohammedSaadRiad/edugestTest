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
 * A Student.
 */
@Table("student")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("identifier")
    private String identifier;

    @Column("birth_date")
    private LocalDate birthDate;

    @Column("gender")
    private Genders gender;

    @Column("nationality")
    private String nationality;

    @Column("phone_number")
    private String phoneNumber;

    @Column("address")
    private String address;

    @Column("note")
    private String note;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "timetable", "teachers", "subjects" }, allowSetters = true)
    private SchoolClass schoolClass;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "students" }, allowSetters = true)
    private Set<Parent> parents = new HashSet<>();

    @Column("school_class_id")
    private Long schoolClassId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Student id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public Student identifier(String identifier) {
        this.setIdentifier(identifier);
        return this;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public Student birthDate(LocalDate birthDate) {
        this.setBirthDate(birthDate);
        return this;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Genders getGender() {
        return this.gender;
    }

    public Student gender(Genders gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(Genders gender) {
        this.gender = gender;
    }

    public String getNationality() {
        return this.nationality;
    }

    public Student nationality(String nationality) {
        this.setNationality(nationality);
        return this;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Student phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return this.address;
    }

    public Student address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return this.note;
    }

    public Student note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public SchoolClass getSchoolClass() {
        return this.schoolClass;
    }

    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
        this.schoolClassId = schoolClass != null ? schoolClass.getId() : null;
    }

    public Student schoolClass(SchoolClass schoolClass) {
        this.setSchoolClass(schoolClass);
        return this;
    }

    public Set<Parent> getParents() {
        return this.parents;
    }

    public void setParents(Set<Parent> parents) {
        this.parents = parents;
    }

    public Student parents(Set<Parent> parents) {
        this.setParents(parents);
        return this;
    }

    public Student addParents(Parent parent) {
        this.parents.add(parent);
        return this;
    }

    public Student removeParents(Parent parent) {
        this.parents.remove(parent);
        return this;
    }

    public Long getSchoolClassId() {
        return this.schoolClassId;
    }

    public void setSchoolClassId(Long schoolClass) {
        this.schoolClassId = schoolClass;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Student)) {
            return false;
        }
        return getId() != null && getId().equals(((Student) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Student{" +
            "id=" + getId() +
            ", identifier='" + getIdentifier() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            ", gender='" + getGender() + "'" +
            ", nationality='" + getNationality() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", address='" + getAddress() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
