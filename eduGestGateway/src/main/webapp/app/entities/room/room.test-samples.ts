import { IRoom, NewRoom } from './room.model';

export const sampleWithRequiredData: IRoom = {
  id: 25051,
  name: 'wire',
};

export const sampleWithPartialData: IRoom = {
  id: 3867,
  name: 'until hm',
  capacity: 8429,
};

export const sampleWithFullData: IRoom = {
  id: 10102,
  name: 'roughly brr opera',
  capacity: 15915,
};

export const sampleWithNewData: NewRoom = {
  name: 'cellar',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
