import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ISchoolClass } from '../school-class.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../school-class.test-samples';

import { SchoolClassService } from './school-class.service';

const requireRestSample: ISchoolClass = {
  ...sampleWithRequiredData,
};

describe('SchoolClass Service', () => {
  let service: SchoolClassService;
  let httpMock: HttpTestingController;
  let expectedResult: ISchoolClass | ISchoolClass[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SchoolClassService);
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

    it('should create a SchoolClass', () => {
      const schoolClass = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(schoolClass).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SchoolClass', () => {
      const schoolClass = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(schoolClass).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SchoolClass', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SchoolClass', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SchoolClass', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSchoolClassToCollectionIfMissing', () => {
      it('should add a SchoolClass to an empty array', () => {
        const schoolClass: ISchoolClass = sampleWithRequiredData;
        expectedResult = service.addSchoolClassToCollectionIfMissing([], schoolClass);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(schoolClass);
      });

      it('should not add a SchoolClass to an array that contains it', () => {
        const schoolClass: ISchoolClass = sampleWithRequiredData;
        const schoolClassCollection: ISchoolClass[] = [
          {
            ...schoolClass,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSchoolClassToCollectionIfMissing(schoolClassCollection, schoolClass);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SchoolClass to an array that doesn't contain it", () => {
        const schoolClass: ISchoolClass = sampleWithRequiredData;
        const schoolClassCollection: ISchoolClass[] = [sampleWithPartialData];
        expectedResult = service.addSchoolClassToCollectionIfMissing(schoolClassCollection, schoolClass);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(schoolClass);
      });

      it('should add only unique SchoolClass to an array', () => {
        const schoolClassArray: ISchoolClass[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const schoolClassCollection: ISchoolClass[] = [sampleWithRequiredData];
        expectedResult = service.addSchoolClassToCollectionIfMissing(schoolClassCollection, ...schoolClassArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const schoolClass: ISchoolClass = sampleWithRequiredData;
        const schoolClass2: ISchoolClass = sampleWithPartialData;
        expectedResult = service.addSchoolClassToCollectionIfMissing([], schoolClass, schoolClass2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(schoolClass);
        expect(expectedResult).toContain(schoolClass2);
      });

      it('should accept null and undefined values', () => {
        const schoolClass: ISchoolClass = sampleWithRequiredData;
        expectedResult = service.addSchoolClassToCollectionIfMissing([], null, schoolClass, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(schoolClass);
      });

      it('should return initial array if no SchoolClass is added', () => {
        const schoolClassCollection: ISchoolClass[] = [sampleWithRequiredData];
        expectedResult = service.addSchoolClassToCollectionIfMissing(schoolClassCollection, undefined, null);
        expect(expectedResult).toEqual(schoolClassCollection);
      });
    });

    describe('compareSchoolClass', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSchoolClass(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 21619 };
        const entity2 = null;

        const compareResult1 = service.compareSchoolClass(entity1, entity2);
        const compareResult2 = service.compareSchoolClass(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 21619 };
        const entity2 = { id: 18628 };

        const compareResult1 = service.compareSchoolClass(entity1, entity2);
        const compareResult2 = service.compareSchoolClass(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 21619 };
        const entity2 = { id: 21619 };

        const compareResult1 = service.compareSchoolClass(entity1, entity2);
        const compareResult2 = service.compareSchoolClass(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
