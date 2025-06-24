import { ITimetable, NewTimetable } from './timetable.model';

export const sampleWithRequiredData: ITimetable = {
  id: 1071,
};

export const sampleWithPartialData: ITimetable = {
  id: 7694,
  semestre: 10875,
};

export const sampleWithFullData: ITimetable = {
  id: 1658,
  semestre: 14510,
};

export const sampleWithNewData: NewTimetable = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
