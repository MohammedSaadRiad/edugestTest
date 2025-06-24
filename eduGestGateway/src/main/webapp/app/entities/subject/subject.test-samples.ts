import { ISubject, NewSubject } from './subject.model';

export const sampleWithRequiredData: ISubject = {
  id: 18046,
  name: 'perfumed whoever',
  code: 'wrathful',
};

export const sampleWithPartialData: ISubject = {
  id: 31257,
  name: 'saloon strong',
  code: 'brightly off',
};

export const sampleWithFullData: ISubject = {
  id: 6439,
  name: 'supportive satirize gripping',
  code: 'meh uh-huh',
  description: 'partial',
};

export const sampleWithNewData: NewSubject = {
  name: 'aboard fairly pike',
  code: 'swiftly afore',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
