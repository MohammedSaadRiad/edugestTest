import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IAdministration } from '../administration.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../administration.test-samples';

import { AdministrationService, RestAdministration } from './administration.service';

const requireRestSample: RestAdministration = {
  ...sampleWithRequiredData,
  birthDate: sampleWithRequiredData.birthDate?.format(DATE_FORMAT),
};

describe('Administration Service', () => {
  let service: AdministrationService;
  let httpMock: HttpTestingController;
  let expectedResult: IAdministration | IAdministration[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(AdministrationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Administration', () => {
      const administration = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(administration).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Administration', () => {
      const administration = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(administration).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Administration', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Administration', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Administration', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addAdministrationToCollectionIfMissing', () => {
      it('should add a Administration to an empty array', () => {
        const administration: IAdministration = sampleWithRequiredData;
        expectedResult = service.addAdministrationToCollectionIfMissing([], administration);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(administration);
      });

      it('should not add a Administration to an array that contains it', () => {
        const administration: IAdministration = sampleWithRequiredData;
        const administrationCollection: IAdministration[] = [
          {
            ...administration,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAdministrationToCollectionIfMissing(administrationCollection, administration);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Administration to an array that doesn't contain it", () => {
        const administration: IAdministration = sampleWithRequiredData;
        const administrationCollection: IAdministration[] = [sampleWithPartialData];
        expectedResult = service.addAdministrationToCollectionIfMissing(administrationCollection, administration);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(administration);
      });

      it('should add only unique Administration to an array', () => {
        const administrationArray: IAdministration[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const administrationCollection: IAdministration[] = [sampleWithRequiredData];
        expectedResult = service.addAdministrationToCollectionIfMissing(administrationCollection, ...administrationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const administration: IAdministration = sampleWithRequiredData;
        const administration2: IAdministration = sampleWithPartialData;
        expectedResult = service.addAdministrationToCollectionIfMissing([], administration, administration2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(administration);
        expect(expectedResult).toContain(administration2);
      });

      it('should accept null and undefined values', () => {
        const administration: IAdministration = sampleWithRequiredData;
        expectedResult = service.addAdministrationToCollectionIfMissing([], null, administration, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(administration);
      });

      it('should return initial array if no Administration is added', () => {
        const administrationCollection: IAdministration[] = [sampleWithRequiredData];
        expectedResult = service.addAdministrationToCollectionIfMissing(administrationCollection, undefined, null);
        expect(expectedResult).toEqual(administrationCollection);
      });
    });

    describe('compareAdministration', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAdministration(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 12826 };
        const entity2 = null;

        const compareResult1 = service.compareAdministration(entity1, entity2);
        const compareResult2 = service.compareAdministration(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 12826 };
        const entity2 = { id: 17862 };

        const compareResult1 = service.compareAdministration(entity1, entity2);
        const compareResult2 = service.compareAdministration(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 12826 };
        const entity2 = { id: 12826 };

        const compareResult1 = service.compareAdministration(entity1, entity2);
        const compareResult2 = service.compareAdministration(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
