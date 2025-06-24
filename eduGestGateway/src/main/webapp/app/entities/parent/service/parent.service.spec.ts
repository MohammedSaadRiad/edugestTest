import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IParent } from '../parent.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../parent.test-samples';

import { ParentService, RestParent } from './parent.service';

const requireRestSample: RestParent = {
  ...sampleWithRequiredData,
  birthDate: sampleWithRequiredData.birthDate?.format(DATE_FORMAT),
};

describe('Parent Service', () => {
  let service: ParentService;
  let httpMock: HttpTestingController;
  let expectedResult: IParent | IParent[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ParentService);
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

    it('should create a Parent', () => {
      const parent = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(parent).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Parent', () => {
      const parent = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(parent).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Parent', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Parent', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Parent', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addParentToCollectionIfMissing', () => {
      it('should add a Parent to an empty array', () => {
        const parent: IParent = sampleWithRequiredData;
        expectedResult = service.addParentToCollectionIfMissing([], parent);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(parent);
      });

      it('should not add a Parent to an array that contains it', () => {
        const parent: IParent = sampleWithRequiredData;
        const parentCollection: IParent[] = [
          {
            ...parent,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addParentToCollectionIfMissing(parentCollection, parent);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Parent to an array that doesn't contain it", () => {
        const parent: IParent = sampleWithRequiredData;
        const parentCollection: IParent[] = [sampleWithPartialData];
        expectedResult = service.addParentToCollectionIfMissing(parentCollection, parent);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(parent);
      });

      it('should add only unique Parent to an array', () => {
        const parentArray: IParent[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const parentCollection: IParent[] = [sampleWithRequiredData];
        expectedResult = service.addParentToCollectionIfMissing(parentCollection, ...parentArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const parent: IParent = sampleWithRequiredData;
        const parent2: IParent = sampleWithPartialData;
        expectedResult = service.addParentToCollectionIfMissing([], parent, parent2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(parent);
        expect(expectedResult).toContain(parent2);
      });

      it('should accept null and undefined values', () => {
        const parent: IParent = sampleWithRequiredData;
        expectedResult = service.addParentToCollectionIfMissing([], null, parent, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(parent);
      });

      it('should return initial array if no Parent is added', () => {
        const parentCollection: IParent[] = [sampleWithRequiredData];
        expectedResult = service.addParentToCollectionIfMissing(parentCollection, undefined, null);
        expect(expectedResult).toEqual(parentCollection);
      });
    });

    describe('compareParent', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareParent(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 10134 };
        const entity2 = null;

        const compareResult1 = service.compareParent(entity1, entity2);
        const compareResult2 = service.compareParent(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 10134 };
        const entity2 = { id: 2353 };

        const compareResult1 = service.compareParent(entity1, entity2);
        const compareResult2 = service.compareParent(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 10134 };
        const entity2 = { id: 10134 };

        const compareResult1 = service.compareParent(entity1, entity2);
        const compareResult2 = service.compareParent(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
