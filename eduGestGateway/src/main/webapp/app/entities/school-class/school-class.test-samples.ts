import { ISchoolClass, NewSchoolClass } from './school-class.model';

export const sampleWithRequiredData: ISchoolClass = {
  id: 31757,
  name: 'yet perfumed',
};

export const sampleWithPartialData: ISchoolClass = {
  id: 31684,
  name: 'impressive gosh',
  year: 10531,
};

export const sampleWithFullData: ISchoolClass = {
  id: 1121,
  name: 'whether reflecting',
  year: 17786,
};

export const sampleWithNewData: NewSchoolClass = {
  name: 'diligently',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
