import dayjs from 'dayjs/esm';

import { IParent, NewParent } from './parent.model';

export const sampleWithRequiredData: IParent = {
  id: 21038,
  identifier: 'retention',
};

export const sampleWithPartialData: IParent = {
  id: 1041,
  identifier: 'ouch augment outgoing',
  gender: 'FEMALE',
  phoneNumber: 'sleepily boohoo',
  address: 'without ugh',
};

export const sampleWithFullData: IParent = {
  id: 1697,
  identifier: 'beyond ouch breed',
  birthDate: dayjs('2025-06-23'),
  gender: 'FEMALE',
  phoneNumber: 'hence',
  address: 'unaccountably',
  note: 'warlike likewise',
};

export const sampleWithNewData: NewParent = {
  identifier: 'secret tragic rightfully',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
