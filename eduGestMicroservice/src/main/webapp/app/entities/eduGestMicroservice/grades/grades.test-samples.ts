import { IGrades, NewGrades } from './grades.model';

export const sampleWithRequiredData: IGrades = {
  id: 26053,
};

export const sampleWithPartialData: IGrades = {
  id: 11195,
  grade: 5588.86,
};

export const sampleWithFullData: IGrades = {
  id: 27128,
  grade: 30858.7,
};

export const sampleWithNewData: NewGrades = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
