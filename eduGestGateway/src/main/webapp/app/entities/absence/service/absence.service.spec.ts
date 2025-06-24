import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IAbsence } from '../absence.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../absence.test-samples';

import { AbsenceService, RestAbsence } from './absence.service';

const requireRestSample: RestAbsence = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.format(DATE_FORMAT),
};

describe('Absence Service', () => {
  let service: AbsenceService;
  let httpMock: HttpTestingController;
  let expectedResult: IAbsence | IAbsence[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(AbsenceService);
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

    it('should create a Absence', () => {
      const absence = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(absence).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Absence', () => {
      const absence = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(absence).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Absence', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Absence', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Absence', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addAbsenceToCollectionIfMissing', () => {
      it('should add a Absence to an empty array', () => {
        const absence: IAbsence = sampleWithRequiredData;
        expectedResult = service.addAbsenceToCollectionIfMissing([], absence);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(absence);
      });

      it('should not add a Absence to an array that contains it', () => {
        const absence: IAbsence = sampleWithRequiredData;
        const absenceCollection: IAbsence[] = [
          {
            ...absence,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAbsenceToCollectionIfMissing(absenceCollection, absence);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Absence to an array that doesn't contain it", () => {
        const absence: IAbsence = sampleWithRequiredData;
        const absenceCollection: IAbsence[] = [sampleWithPartialData];
        expectedResult = service.addAbsenceToCollectionIfMissing(absenceCollection, absence);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(absence);
      });

      it('should add only unique Absence to an array', () => {
        const absenceArray: IAbsence[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const absenceCollection: IAbsence[] = [sampleWithRequiredData];
        expectedResult = service.addAbsenceToCollectionIfMissing(absenceCollection, ...absenceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const absence: IAbsence = sampleWithRequiredData;
        const absence2: IAbsence = sampleWithPartialData;
        expectedResult = service.addAbsenceToCollectionIfMissing([], absence, absence2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(absence);
        expect(expectedResult).toContain(absence2);
      });

      it('should accept null and undefined values', () => {
        const absence: IAbsence = sampleWithRequiredData;
        expectedResult = service.addAbsenceToCollectionIfMissing([], null, absence, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(absence);
      });

      it('should return initial array if no Absence is added', () => {
        const absenceCollection: IAbsence[] = [sampleWithRequiredData];
        expectedResult = service.addAbsenceToCollectionIfMissing(absenceCollection, undefined, null);
        expect(expectedResult).toEqual(absenceCollection);
      });
    });

    describe('compareAbsence', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAbsence(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 3675 };
        const entity2 = null;

        const compareResult1 = service.compareAbsence(entity1, entity2);
        const compareResult2 = service.compareAbsence(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 3675 };
        const entity2 = { id: 9045 };

        const compareResult1 = service.compareAbsence(entity1, entity2);
        const compareResult2 = service.compareAbsence(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 3675 };
        const entity2 = { id: 3675 };

        const compareResult1 = service.compareAbsence(entity1, entity2);
        const compareResult2 = service.compareAbsence(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
