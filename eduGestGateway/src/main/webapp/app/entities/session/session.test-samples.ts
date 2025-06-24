import { ISession, NewSession } from './session.model';

export const sampleWithRequiredData: ISession = {
  id: 11389,
};

export const sampleWithPartialData: ISession = {
  id: 24025,
  semester: 205,
};

export const sampleWithFullData: ISession = {
  id: 812,
  day: 'made-up ajar ecstatic',
  startTime: 'recent spark',
  endTime: 'oof until so',
  semester: 2437,
};

export const sampleWithNewData: NewSession = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
