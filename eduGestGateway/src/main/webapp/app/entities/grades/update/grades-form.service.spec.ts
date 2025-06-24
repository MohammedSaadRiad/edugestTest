import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../grades.test-samples';

import { GradesFormService } from './grades-form.service';

describe('Grades Form Service', () => {
  let service: GradesFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GradesFormService);
  });

  describe('Service methods', () => {
    describe('createGradesFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createGradesFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            grade: expect.any(Object),
            student: expect.any(Object),
            subject: expect.any(Object),
          }),
        );
      });

      it('passing IGrades should create a new form with FormGroup', () => {
        const formGroup = service.createGradesFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            grade: expect.any(Object),
            student: expect.any(Object),
            subject: expect.any(Object),
          }),
        );
      });
    });

    describe('getGrades', () => {
      it('should return NewGrades for default Grades initial value', () => {
        const formGroup = service.createGradesFormGroup(sampleWithNewData);

        const grades = service.getGrades(formGroup) as any;

        expect(grades).toMatchObject(sampleWithNewData);
      });

      it('should return NewGrades for empty Grades initial value', () => {
        const formGroup = service.createGradesFormGroup();

        const grades = service.getGrades(formGroup) as any;

        expect(grades).toMatchObject({});
      });

      it('should return IGrades', () => {
        const formGroup = service.createGradesFormGroup(sampleWithRequiredData);

        const grades = service.getGrades(formGroup) as any;

        expect(grades).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IGrades should not enable id FormControl', () => {
        const formGroup = service.createGradesFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewGrades should disable id FormControl', () => {
        const formGroup = service.createGradesFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
