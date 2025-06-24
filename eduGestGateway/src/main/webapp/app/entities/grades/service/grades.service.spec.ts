import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IGrades } from '../grades.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../grades.test-samples';

import { GradesService } from './grades.service';

const requireRestSample: IGrades = {
  ...sampleWithRequiredData,
};

describe('Grades Service', () => {
  let service: GradesService;
  let httpMock: HttpTestingController;
  let expectedResult: IGrades | IGrades[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(GradesService);
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

    it('should create a Grades', () => {
      const grades = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(grades).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Grades', () => {
      const grades = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(grades).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Grades', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Grades', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Grades', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addGradesToCollectionIfMissing', () => {
      it('should add a Grades to an empty array', () => {
        const grades: IGrades = sampleWithRequiredData;
        expectedResult = service.addGradesToCollectionIfMissing([], grades);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(grades);
      });

      it('should not add a Grades to an array that contains it', () => {
        const grades: IGrades = sampleWithRequiredData;
        const gradesCollection: IGrades[] = [
          {
            ...grades,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addGradesToCollectionIfMissing(gradesCollection, grades);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Grades to an array that doesn't contain it", () => {
        const grades: IGrades = sampleWithRequiredData;
        const gradesCollection: IGrades[] = [sampleWithPartialData];
        expectedResult = service.addGradesToCollectionIfMissing(gradesCollection, grades);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(grades);
      });

      it('should add only unique Grades to an array', () => {
        const gradesArray: IGrades[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const gradesCollection: IGrades[] = [sampleWithRequiredData];
        expectedResult = service.addGradesToCollectionIfMissing(gradesCollection, ...gradesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const grades: IGrades = sampleWithRequiredData;
        const grades2: IGrades = sampleWithPartialData;
        expectedResult = service.addGradesToCollectionIfMissing([], grades, grades2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(grades);
        expect(expectedResult).toContain(grades2);
      });

      it('should accept null and undefined values', () => {
        const grades: IGrades = sampleWithRequiredData;
        expectedResult = service.addGradesToCollectionIfMissing([], null, grades, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(grades);
      });

      it('should return initial array if no Grades is added', () => {
        const gradesCollection: IGrades[] = [sampleWithRequiredData];
        expectedResult = service.addGradesToCollectionIfMissing(gradesCollection, undefined, null);
        expect(expectedResult).toEqual(gradesCollection);
      });
    });

    describe('compareGrades', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareGrades(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 25513 };
        const entity2 = null;

        const compareResult1 = service.compareGrades(entity1, entity2);
        const compareResult2 = service.compareGrades(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 25513 };
        const entity2 = { id: 26965 };

        const compareResult1 = service.compareGrades(entity1, entity2);
        const compareResult2 = service.compareGrades(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 25513 };
        const entity2 = { id: 25513 };

        const compareResult1 = service.compareGrades(entity1, entity2);
        const compareResult2 = service.compareGrades(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
