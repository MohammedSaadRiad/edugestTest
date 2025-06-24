import dayjs from 'dayjs/esm';

import { IStudent, NewStudent } from './student.model';

export const sampleWithRequiredData: IStudent = {
  id: 15380,
  identifier: 'new instead married',
};

export const sampleWithPartialData: IStudent = {
  id: 3475,
  identifier: 'appropriate psst',
  birthDate: dayjs('2025-06-23'),
  nationality: 'list irritably',
  phoneNumber: 'plait',
  note: 'wallop',
};

export const sampleWithFullData: IStudent = {
  id: 6617,
  identifier: 'pleasant plain petty',
  birthDate: dayjs('2025-06-23'),
  gender: 'FEMALE',
  nationality: 'against new',
  phoneNumber: 'between',
  address: 'freely',
  note: 'infinite whenever',
};

export const sampleWithNewData: NewStudent = {
  identifier: 'gad even aboard',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
