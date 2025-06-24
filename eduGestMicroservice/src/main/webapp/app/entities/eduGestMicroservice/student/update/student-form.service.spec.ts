import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../student.test-samples';

import { StudentFormService } from './student-form.service';

describe('Student Form Service', () => {
  let service: StudentFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StudentFormService);
  });

  describe('Service methods', () => {
    describe('createStudentFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createStudentFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            identifier: expect.any(Object),
            birthDate: expect.any(Object),
            gender: expect.any(Object),
            nationality: expect.any(Object),
            phoneNumber: expect.any(Object),
            address: expect.any(Object),
            note: expect.any(Object),
            schoolClass: expect.any(Object),
            parents: expect.any(Object),
          }),
        );
      });

      it('passing IStudent should create a new form with FormGroup', () => {
        const formGroup = service.createStudentFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            identifier: expect.any(Object),
            birthDate: expect.any(Object),
            gender: expect.any(Object),
            nationality: expect.any(Object),
            phoneNumber: expect.any(Object),
            address: expect.any(Object),
            note: expect.any(Object),
            schoolClass: expect.any(Object),
            parents: expect.any(Object),
          }),
        );
      });
    });

    describe('getStudent', () => {
      it('should return NewStudent for default Student initial value', () => {
        const formGroup = service.createStudentFormGroup(sampleWithNewData);

        const student = service.getStudent(formGroup) as any;

        expect(student).toMatchObject(sampleWithNewData);
      });

      it('should return NewStudent for empty Student initial value', () => {
        const formGroup = service.createStudentFormGroup();

        const student = service.getStudent(formGroup) as any;

        expect(student).toMatchObject({});
      });

      it('should return IStudent', () => {
        const formGroup = service.createStudentFormGroup(sampleWithRequiredData);

        const student = service.getStudent(formGroup) as any;

        expect(student).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IStudent should not enable id FormControl', () => {
        const formGroup = service.createStudentFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewStudent should disable id FormControl', () => {
        const formGroup = service.createStudentFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
