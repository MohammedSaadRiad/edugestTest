import dayjs from 'dayjs/esm';

import { IAbsence, NewAbsence } from './absence.model';

export const sampleWithRequiredData: IAbsence = {
  id: 9640,
};

export const sampleWithPartialData: IAbsence = {
  id: 76,
  date: dayjs('2025-06-23'),
};

export const sampleWithFullData: IAbsence = {
  id: 8086,
  date: dayjs('2025-06-24'),
  justification: 'stratify',
  note: 'once for onto',
};

export const sampleWithNewData: NewAbsence = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
