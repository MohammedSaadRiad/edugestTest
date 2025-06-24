import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../school-class.test-samples';

import { SchoolClassFormService } from './school-class-form.service';

describe('SchoolClass Form Service', () => {
  let service: SchoolClassFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SchoolClassFormService);
  });

  describe('Service methods', () => {
    describe('createSchoolClassFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSchoolClassFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            year: expect.any(Object),
            timetable: expect.any(Object),
            teachers: expect.any(Object),
            subjects: expect.any(Object),
          }),
        );
      });

      it('passing ISchoolClass should create a new form with FormGroup', () => {
        const formGroup = service.createSchoolClassFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            year: expect.any(Object),
            timetable: expect.any(Object),
            teachers: expect.any(Object),
            subjects: expect.any(Object),
          }),
        );
      });
    });

    describe('getSchoolClass', () => {
      it('should return NewSchoolClass for default SchoolClass initial value', () => {
        const formGroup = service.createSchoolClassFormGroup(sampleWithNewData);

        const schoolClass = service.getSchoolClass(formGroup) as any;

        expect(schoolClass).toMatchObject(sampleWithNewData);
      });

      it('should return NewSchoolClass for empty SchoolClass initial value', () => {
        const formGroup = service.createSchoolClassFormGroup();

        const schoolClass = service.getSchoolClass(formGroup) as any;

        expect(schoolClass).toMatchObject({});
      });

      it('should return ISchoolClass', () => {
        const formGroup = service.createSchoolClassFormGroup(sampleWithRequiredData);

        const schoolClass = service.getSchoolClass(formGroup) as any;

        expect(schoolClass).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISchoolClass should not enable id FormControl', () => {
        const formGroup = service.createSchoolClassFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSchoolClass should disable id FormControl', () => {
        const formGroup = service.createSchoolClassFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
