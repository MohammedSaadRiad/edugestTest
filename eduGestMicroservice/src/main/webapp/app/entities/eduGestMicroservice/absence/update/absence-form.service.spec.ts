import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../absence.test-samples';

import { AbsenceFormService } from './absence-form.service';

describe('Absence Form Service', () => {
  let service: AbsenceFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AbsenceFormService);
  });

  describe('Service methods', () => {
    describe('createAbsenceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAbsenceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            justification: expect.any(Object),
            note: expect.any(Object),
            student: expect.any(Object),
            session: expect.any(Object),
          }),
        );
      });

      it('passing IAbsence should create a new form with FormGroup', () => {
        const formGroup = service.createAbsenceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            justification: expect.any(Object),
            note: expect.any(Object),
            student: expect.any(Object),
            session: expect.any(Object),
          }),
        );
      });
    });

    describe('getAbsence', () => {
      it('should return NewAbsence for default Absence initial value', () => {
        const formGroup = service.createAbsenceFormGroup(sampleWithNewData);

        const absence = service.getAbsence(formGroup) as any;

        expect(absence).toMatchObject(sampleWithNewData);
      });

      it('should return NewAbsence for empty Absence initial value', () => {
        const formGroup = service.createAbsenceFormGroup();

        const absence = service.getAbsence(formGroup) as any;

        expect(absence).toMatchObject({});
      });

      it('should return IAbsence', () => {
        const formGroup = service.createAbsenceFormGroup(sampleWithRequiredData);

        const absence = service.getAbsence(formGroup) as any;

        expect(absence).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAbsence should not enable id FormControl', () => {
        const formGroup = service.createAbsenceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAbsence should disable id FormControl', () => {
        const formGroup = service.createAbsenceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
