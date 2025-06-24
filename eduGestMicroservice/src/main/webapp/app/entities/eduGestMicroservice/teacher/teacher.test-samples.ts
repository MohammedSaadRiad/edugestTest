import dayjs from 'dayjs/esm';

import { ITeacher, NewTeacher } from './teacher.model';

export const sampleWithRequiredData: ITeacher = {
  id: 9510,
  identifier: 'preclude unselfish legal',
};

export const sampleWithPartialData: ITeacher = {
  id: 7772,
  identifier: 'out',
  phoneNumber: 'upward considerate internalize',
  type: 'oof',
};

export const sampleWithFullData: ITeacher = {
  id: 30957,
  identifier: 'lest consequently',
  birthDate: dayjs('2025-06-23'),
  qualification: 'that despite carefully',
  gender: 'MALE',
  experience: 4777,
  phoneNumber: 'holster including midwife',
  address: 'reborn ill government',
  type: 'delight a scarcely',
  note: 'save',
};

export const sampleWithNewData: NewTeacher = {
  identifier: 'since painfully or',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
