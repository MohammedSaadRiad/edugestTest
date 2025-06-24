import dayjs from 'dayjs/esm';

import { IAdministration, NewAdministration } from './administration.model';

export const sampleWithRequiredData: IAdministration = {
  id: 11574,
  identifier: 'edge',
};

export const sampleWithPartialData: IAdministration = {
  id: 7848,
  identifier: 'vibrant that',
  birthDate: dayjs('2025-06-23'),
  phoneNumber: 'joshingly than',
  type: 'intermesh where helpfully',
  note: 'calculus',
};

export const sampleWithFullData: IAdministration = {
  id: 23979,
  identifier: 'on mixture',
  birthDate: dayjs('2025-06-23'),
  gender: 'MALE',
  phoneNumber: 'soup',
  address: 'meh nor bravely',
  type: 'huzzah',
  note: 'knickers really boldly',
};

export const sampleWithNewData: NewAdministration = {
  identifier: 'for once happy',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
