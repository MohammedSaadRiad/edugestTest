import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../administration.test-samples';

import { AdministrationFormService } from './administration-form.service';

describe('Administration Form Service', () => {
  let service: AdministrationFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdministrationFormService);
  });

  describe('Service methods', () => {
    describe('createAdministrationFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAdministrationFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            identifier: expect.any(Object),
            birthDate: expect.any(Object),
            gender: expect.any(Object),
            phoneNumber: expect.any(Object),
            address: expect.any(Object),
            type: expect.any(Object),
            note: expect.any(Object),
          }),
        );
      });

      it('passing IAdministration should create a new form with FormGroup', () => {
        const formGroup = service.createAdministrationFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            identifier: expect.any(Object),
            birthDate: expect.any(Object),
            gender: expect.any(Object),
            phoneNumber: expect.any(Object),
            address: expect.any(Object),
            type: expect.any(Object),
            note: expect.any(Object),
          }),
        );
      });
    });

    describe('getAdministration', () => {
      it('should return NewAdministration for default Administration initial value', () => {
        const formGroup = service.createAdministrationFormGroup(sampleWithNewData);

        const administration = service.getAdministration(formGroup) as any;

        expect(administration).toMatchObject(sampleWithNewData);
      });

      it('should return NewAdministration for empty Administration initial value', () => {
        const formGroup = service.createAdministrationFormGroup();

        const administration = service.getAdministration(formGroup) as any;

        expect(administration).toMatchObject({});
      });

      it('should return IAdministration', () => {
        const formGroup = service.createAdministrationFormGroup(sampleWithRequiredData);

        const administration = service.getAdministration(formGroup) as any;

        expect(administration).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAdministration should not enable id FormControl', () => {
        const formGroup = service.createAdministrationFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAdministration should disable id FormControl', () => {
        const formGroup = service.createAdministrationFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
